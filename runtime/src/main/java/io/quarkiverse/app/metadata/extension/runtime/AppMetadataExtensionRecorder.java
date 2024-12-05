package io.quarkiverse.app.metadata.extension.runtime;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class AppMetadataExtensionRecorder {

    public RuntimeValue<AppMetadataWrapper> createAppMetadataWrapper(AppMetadata metadata) {
        AppMetadataWrapper wrapper = new AppMetadataWrapper();
        wrapper.setAppMetadata(metadata);
        return new RuntimeValue<>(wrapper);
    }

    public Handler<RoutingContext> createHandler(AppMetadata data) {
        return new AppMetadataHandler(data);
    }
}
