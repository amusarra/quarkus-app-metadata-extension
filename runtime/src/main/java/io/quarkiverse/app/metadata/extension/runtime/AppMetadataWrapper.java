package io.quarkiverse.app.metadata.extension.runtime;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * A wrapper class for AppMetadata.
 * This class is annotated with @ApplicationScoped to indicate that it is a CDI bean with application scope.
 *
 * @see AppMetadata
 *
 * @author Antonio Musarra
 */
@ApplicationScoped
public class AppMetadataWrapper {
    private AppMetadata appMetadata;

    /**
     * Gets the AppMetadata.
     *
     * @return the AppMetadata object.
     */
    public AppMetadata getAppMetadata() {
        return appMetadata;
    }

    /**
     * Sets the AppMetadata.
     *
     * @param appMetadata the AppMetadata object to set.
     */
    public void setAppMetadata(AppMetadata appMetadata) {
        this.appMetadata = appMetadata;
    }
}
