package io.quarkiverse.app.metadata.extension.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class AppMetadataHandler implements Handler<RoutingContext> {

    private final String data;

    public AppMetadataHandler(AppMetadata metadata) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.response().putHeader("Content-Type", "application/json");
        routingContext.end(data);
    }
}
