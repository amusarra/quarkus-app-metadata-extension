package io.quarkiverse.app.metadata.extension.deployment;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkiverse.app.metadata.extension.runtime.AppMetadataExtensionRecorder;
import io.quarkus.bootstrap.model.ApplicationModel;
import io.quarkus.deployment.BootstrapConfig;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AppModelProviderBuildItem;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.logging.Log;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

class AppMetadataExtensionProcessor {

    private static final String FEATURE = "app-metadata-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    RouteBuildItem createRoute(
            AppMetadataExtensionRecorder recorder,
            ApplicationInfoBuildItem info,
            AppModelProviderBuildItem appModel,
            AppMetadataExtensionConfig config) {

        ApplicationModel applicationModel = appModel.validateAndGet(new BootstrapConfig());

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("name", info.getName());
        json.put("groupId", applicationModel.getAppArtifact().getGroupId());
        json.put("artifactId", applicationModel.getAppArtifact().getArtifactId());
        json.put("version", info.getVersion());

        if (config.platformInfo()) {
            ArrayNode platformInfoArray = json.putArray("platform-info");
            applicationModel
                    .getPlatforms()
                    .getPlatformReleaseInfo()
                    .forEach(
                            platformReleaseInfo -> platformInfoArray
                                    .addObject()
                                    .put("platform", platformReleaseInfo.getPlatformKey())
                                    .put("version", platformReleaseInfo.getVersion()));
        }

        if (config.dependencies()) {
            ArrayNode dependenciesArray = json.putArray("runtime-dependencies");
            applicationModel
                    .getRuntimeDependencies()
                    .forEach(
                            dep -> dependenciesArray
                                    .addObject()
                                    .put("artifactId", dep.getArtifactId())
                                    .put("groupId", dep.getGroupId()));
        }

        if (config.javaBuildInfo()) {
            ArrayNode javaBuildInfo = json.putArray("javaBuildInfo");
            javaBuildInfo
                    .addObject()
                    .put("javaVendor", System.getProperty("java.vendor"))
                    .put("javaVendorVersion", System.getProperty("java.vendor.version"))
                    .put("javaVersion", System.getProperty("java.version"))
                    .put("osArch", System.getProperty("os.arch"))
                    .put("osName", System.getProperty("os.name"))
                    .put("osVersion", System.getProperty("os.version"))
                    .put("buildTime", System.currentTimeMillis());
        }

        if (config.scmInfo()) {
            try {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                Repository repository = builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();

                try (Git git = new Git(repository)) {
                    // Get the current branch
                    String branch = repository.getBranch();
                    Log.infof("Current branch: %s", branch);

                    // Get the current tag
                    String currentTag = git.describe().setTags(true).call();
                    Log.infof("Current tag: %s", currentTag);

                    // Get the latest commit
                    RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
                    String commitId = latestCommit.getName();
                    String commitDate = latestCommit.getAuthorIdent().getWhen().toString();
                    String authorName = latestCommit.getAuthorIdent().getName();
                    String authorEmail = latestCommit.getAuthorIdent().getEmailAddress();
                    Log.infof("Latest commit: %s", commitId);
                    Log.infof("Latest commit date: %s", commitDate);
                    Log.infof("Author: %s <%s>", authorName, authorEmail);

                    // Get the remote URL
                    String remoteUrl = repository.getConfig().getString("remote", "origin", "url");
                    Log.infof("Remote URL origin: %s", remoteUrl);

                    ArrayNode scmInfo = json.putArray("scmInfo");
                    scmInfo
                            .addObject()
                            .put("branch", branch)
                            .put("tag", currentTag)
                            .put("commitId", commitId)
                            .put("commitDate", commitDate)
                            .put("authorName", authorName)
                            .put("authorEmail", authorEmail)
                            .put("remoteUrl", remoteUrl);
                }
            } catch (IOException | GitAPIException e) {
                Log.infof("Error accessing Git repository: %s", e.getMessage());
            }
        }

        Handler<RoutingContext> handler = recorder.createHandler(json.toString());

        return new RouteBuildItem.Builder().handler(handler).route(config.path()).build();
    }
}
