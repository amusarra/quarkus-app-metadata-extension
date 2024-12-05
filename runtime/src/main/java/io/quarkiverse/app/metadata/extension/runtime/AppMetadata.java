package io.quarkiverse.app.metadata.extension.runtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Record class representing application metadata.
 * This class includes information about the application, platform, dependencies, Java build, and SCM.
 *
 * @author Antonio Musarra
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppMetadata(
        String name,
        String groupId,
        String artifactId,
        String version,
        List<PlatformInfo> platformInfo,
        List<Dependency> dependencies,
        List<JavaBuildInfo> javaBuildInfo,
        ScmInfo scmInfo) {

    /**
     * Record class representing platform information.
     */
    public record PlatformInfo(String platform, String version) {
    }

    /**
     * Record class representing dependency information.
     */
    public record Dependency(String artifactId, String groupId) {
    }

    /**
     * Record class representing Java build information.
     */
    public record JavaBuildInfo(
            String javaVendor,
            String javaVendorVersion,
            String javaVersion,
            String osArch,
            String osName,
            String osVersion,
            long buildTime) {
    }

    /**
     * Record class representing SCM (Source Control Management) information.
     */
    public record ScmInfo(
            String branch,
            String tag,
            String commitId,
            String commitDate,
            String authorName,
            String authorEmail,
            String remoteUrl) {
    }
}