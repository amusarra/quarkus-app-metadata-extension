package io.quarkiverse.app.metadata.extension.runtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    public record PlatformInfo(String platform, String version) {
    }

    public record Dependency(String artifactId, String groupId) {
    }

    public record JavaBuildInfo(
            String javaVendor,
            String javaVendorVersion,
            String javaVersion,
            String osArch,
            String osName,
            String osVersion,
            long buildTime) {
    }

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
