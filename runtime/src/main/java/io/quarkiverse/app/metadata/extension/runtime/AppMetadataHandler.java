package io.quarkiverse.app.metadata.extension.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Handler for processing AppMetadata and returning it as a JSON response.
 *
 * @author Antonio Musarra
 */
public class AppMetadataHandler implements Handler<RoutingContext> {

    private final String data;

    /**
     * Constructs an AppMetadataHandler.
     *
     * @param metadata the AppMetadata object to be converted to JSON.
     */
    public AppMetadataHandler(AppMetadata metadata) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the incoming RoutingContext by setting the response content type to JSON
     * and writing the AppMetadata JSON data to the response.
     *
     * @param routingContext the RoutingContext to be handled.
     */
    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.response().putHeader("Content-Type", "application/json");
        routingContext.end(data);
    }
}
