package io.quarkiverse.app.metadata.extension.runtime;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class AppMetadataHandler implements Handler<RoutingContext> {

    private final String data;

    public AppMetadataHandler(String data) {
        this.data = data;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.response().putHeader("Content-Type", "application/json");
        routingContext.end(data);
    }
}
