package io.quarkiverse.app.metadata.extension.runtime;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppMetadataWrapper {
    private AppMetadata appMetadata;

    public AppMetadata getAppMetadata() {
        return appMetadata;
    }

    public void setAppMetadata(AppMetadata appMetadata) {
        this.appMetadata = appMetadata;
    }
}
