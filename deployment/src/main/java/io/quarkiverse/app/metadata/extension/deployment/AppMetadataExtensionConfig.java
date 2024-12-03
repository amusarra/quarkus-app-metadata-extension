package io.quarkiverse.app.metadata.extension.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.app.metadata")
public interface AppMetadataExtensionConfig {

    /** The path to the metadata endpoint. */
    @WithDefault("/app-metadata")
    String path();
}
