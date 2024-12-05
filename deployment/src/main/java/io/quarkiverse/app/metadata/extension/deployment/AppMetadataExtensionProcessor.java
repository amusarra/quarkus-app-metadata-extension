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

/**
 * Processor class for the App Metadata Extension. This class contains build steps to configure and
 * create routes for the metadata endpoint.
 *
 * @author Antonio Musarra
 */
class AppMetadataExtensionProcessor {

    private static final String FEATURE = "app-metadata-extension";

    /**
     * Build step to register the feature.
     *
     * @return a FeatureBuildItem representing the feature.
     */
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Build step to create a route for the metadata endpoint.
     *
     * @param recorder the recorder to create runtime values.
     * @param info the application info.
     * @param appModelProvider the application model provider.
     * @param config the configuration for the extension.
     * @param syntheticBeans the build producer for synthetic beans.
     * @return a RouteBuildItem representing the route.
     */
    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    RouteBuildItem createRoute(
            AppMetadataExtensionRecorder recorder,
            ApplicationInfoBuildItem info,
            AppModelProviderBuildItem appModelProvider,
            AppMetadataExtensionConfig config,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {

        ApplicationModel appModel = appModelProvider.validateAndGet(new BootstrapConfig());

        AppMetadata metadata = new AppMetadata(
                info.getName(),
                appModel.getAppArtifact().getGroupId(),
                appModel.getAppArtifact().getArtifactId(),
                info.getVersion(),
                config.platformInfo()
                        ? appModel.getPlatforms().getPlatformReleaseInfo().stream()
                                .map(
                                        platformReleaseInfo -> new AppMetadata.PlatformInfo(
                                                platformReleaseInfo.getPlatformKey(),
                                                platformReleaseInfo.getVersion()))
                                .toList()
                        : null,
                config.dependencies()
                        ? appModel.getRuntimeDependencies().stream()
                                .map(dep -> new AppMetadata.Dependency(dep.getArtifactId(), dep.getGroupId()))
                                .toList()
                        : null,
                config.javaBuildInfo()
                        ? List.of(
                                new AppMetadata.JavaBuildInfo(
                                        System.getProperty("java.vendor"),
                                        System.getProperty("java.vendor.version"),
                                        System.getProperty("java.version"),
                                        System.getProperty("os.arch"),
                                        System.getProperty("os.name"),
                                        System.getProperty("os.version"),
                                        System.currentTimeMillis()))
                        : null,
                config.scmInfo() ? getScmInfo() : null);

        // Use the recorder to create a runtime proxy of AppMetadataWrapper
        RuntimeValue<AppMetadataWrapper> runtimeMetadataWrapper = recorder.createAppMetadataWrapper(metadata);

        // Register the runtime proxy as a synthetic bean
        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(AppMetadataWrapper.class)
                        .scope(ApplicationScoped.class)
                        .runtimeValue(runtimeMetadataWrapper)
                        .unremovable()
                        .done());

        Handler<RoutingContext> handler = recorder.createHandler(metadata);

        return new RouteBuildItem.Builder().handler(handler).route(config.path()).build();
    }

    /**
     * Retrieves SCM (git) information.
     *
     * @return an AppMetadata.ScmInfo object containing SCM information.
     */
    private AppMetadata.ScmInfo getScmInfo() {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();

            try (Git git = new Git(repository)) {
                String branch = repository.getBranch();
                String currentTag = git.describe().setTags(true).call();
                RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
                String commitId = latestCommit.getName();
                String commitDate = latestCommit.getAuthorIdent().getWhenAsInstant().toString();
                String authorName = latestCommit.getAuthorIdent().getName();
                String authorEmail = latestCommit.getAuthorIdent().getEmailAddress();
                String remoteUrl = repository.getConfig().getString("remote", "origin", "url");

                return new AppMetadata.ScmInfo(
                        branch, currentTag, commitId, commitDate, authorName, authorEmail, remoteUrl);
            }
        } catch (IOException | GitAPIException e) {
            Log.infof("Error accessing Git repository: %s", e.getMessage());
            return null;
        }
    }
}
