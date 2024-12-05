package io.quarkiverse.app.metadata.extension.runtime;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Recorder class for the App Metadata Extension.
 * This class contains methods to create runtime values and handlers for the metadata.
 *
 * @author Antonio Musarra
 */
@Recorder
public class AppMetadataExtensionRecorder {

    /**
     * Creates a runtime value for AppMetadataWrapper.
     *
     * @param metadata the AppMetadata object to be wrapped.
     * @return a RuntimeValue containing the AppMetadataWrapper.
     */
    public RuntimeValue<AppMetadataWrapper> createAppMetadataWrapper(AppMetadata metadata) {
        AppMetadataWrapper wrapper = new AppMetadataWrapper();
        wrapper.setAppMetadata(metadata);
        return new RuntimeValue<>(wrapper);
    }

    /**
     * Creates a handler for the AppMetadata.
     *
     * @param data the AppMetadata object to be handled.
     * @return a Handler for the RoutingContext that processes the AppMetadata.
     */
    public Handler<RoutingContext> createHandler(AppMetadata data) {
        return new AppMetadataHandler(data);
    }
}
