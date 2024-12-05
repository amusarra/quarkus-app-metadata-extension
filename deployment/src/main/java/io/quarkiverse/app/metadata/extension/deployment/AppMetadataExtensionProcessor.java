package io.quarkiverse.app.metadata.extension.deployment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import io.quarkiverse.app.metadata.extension.runtime.AppMetadata;
import io.quarkiverse.app.metadata.extension.runtime.AppMetadataExtensionRecorder;
import io.quarkiverse.app.metadata.extension.runtime.AppMetadataWrapper;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.bootstrap.model.ApplicationModel;
import io.quarkus.deployment.BootstrapConfig;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AppModelProviderBuildItem;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.logging.Log;
import io.quarkus.runtime.RuntimeValue;
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
            AppMetadataExtensionConfig config, BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {

        ApplicationModel applicationModel = appModel.validateAndGet(new BootstrapConfig());

        AppMetadata metadata = new AppMetadata(
                info.getName(),
                applicationModel.getAppArtifact().getGroupId(),
                applicationModel.getAppArtifact().getArtifactId(),
                info.getVersion(),
                config.platformInfo() ? applicationModel.getPlatforms().getPlatformReleaseInfo().stream()
                        .map(platformReleaseInfo -> new AppMetadata.PlatformInfo(platformReleaseInfo.getPlatformKey(),
                                platformReleaseInfo.getVersion()))
                        .toList() : null,
                config.dependencies() ? applicationModel.getRuntimeDependencies().stream()
                        .map(dep -> new AppMetadata.Dependency(dep.getArtifactId(), dep.getGroupId()))
                        .toList() : null,
                config.javaBuildInfo() ? List.of(new AppMetadata.JavaBuildInfo(
                        System.getProperty("java.vendor"),
                        System.getProperty("java.vendor.version"),
                        System.getProperty("java.version"),
                        System.getProperty("os.arch"),
                        System.getProperty("os.name"),
                        System.getProperty("os.version"),
                        System.currentTimeMillis())) : null,
                config.scmInfo() ? getScmInfo() : null);

        // Utilizza il recorder per creare un proxy runtime di AppMetadataWrapper
        RuntimeValue<AppMetadataWrapper> runtimeMetadataWrapper = recorder.createAppMetadataWrapper(metadata);

        // Registra il bean come singleton
        syntheticBeans.produce(SyntheticBeanBuildItem.configure(AppMetadataWrapper.class)
                .scope(ApplicationScoped.class)
                .runtimeValue(runtimeMetadataWrapper)
                .unremovable()
                .done());

        Handler<RoutingContext> handler = recorder.createHandler(metadata);

        return new RouteBuildItem.Builder().handler(handler).route(config.path()).build();
    }

    private AppMetadata.ScmInfo getScmInfo() {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();

            try (Git git = new Git(repository)) {
                String branch = repository.getBranch();
                String currentTag = git.describe().setTags(true).call();
                RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
                String commitId = latestCommit.getName();
                String commitDate = latestCommit.getAuthorIdent().getWhen().toString();
                String authorName = latestCommit.getAuthorIdent().getName();
                String authorEmail = latestCommit.getAuthorIdent().getEmailAddress();
                String remoteUrl = repository.getConfig().getString("remote", "origin", "url");

                return new AppMetadata.ScmInfo(branch, currentTag, commitId, commitDate, authorName, authorEmail, remoteUrl);
            }
        } catch (IOException | GitAPIException e) {
            Log.infof("Error accessing Git repository: %s", e.getMessage());
            return null;
        }
    }
}
