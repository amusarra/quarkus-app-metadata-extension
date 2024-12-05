package io.quarkiverse.app.metadata.extension.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.application.metadata")
public interface AppMetadataExtensionConfig {

    /** The path to the metadata endpoint. */
    @WithDefault("/app-metadata")
    String path();

    /**
     * Whether to include platform information in the metadata.
     */
    @WithDefault("true")
    boolean platformInfo();

    /**
     * Whether to include dependencies in the metadata.
     */
    @WithDefault("false")
    boolean dependencies();

    /**
     * Whether to include the Quarkus java build info in the metadata.
     */
    @WithDefault("true")
    boolean javaBuildInfo();

    /**
     * Whether to include the SCM (git) info in the metadata.
     */
    @WithDefault("true")
    boolean scmInfo();

}
