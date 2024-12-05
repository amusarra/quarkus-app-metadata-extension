# Quarkus Application Metadata Extension

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse/quarkus-app-metadata-extension?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse/quarkus-app-metadata-extension-parent)

## Overview

The Quarkus Application Metadata Extension provides a way to manage and expose metadata about your Quarkus application. This includes information about the application itself, its dependencies, platform details, Java build information, and source control management (SCM) details.

## Features

- **AppMetadataHandler**: Handles HTTP requests and returns application metadata as a JSON response.
- **AppMetadataWrapper**: A CDI bean that wraps the `AppMetadata` object.
- **AppMetadata**: A record class that holds various metadata about the application, including platform info, dependencies, Java build info, and SCM info.

## Extension details

### AppMetadataHandler

The `AppMetadataHandler` class processes `AppMetadata` and returns it as a JSON response. It is typically used in a Quarkus application to expose metadata via an HTTP endpoint. The default path for the metadata endpoint is `/app-metadata`; the path can be configured using the `quarkus.application.metadata.path` property.

### AppMetadataWrapper

The `AppMetadataWrapper` class is a CDI bean with application scope that wraps the `AppMetadata` object. It provides getter and setter methods to access and modify the metadata.

### AppMetadata

The `AppMetadata` record class includes the following information:
- **name**: The name of the application.
- **groupId**: The group ID of the application artifact.
- **artifactId**: The artifact ID of the application artifact.
- **version**: The version of the application.
- **platformInfo**: A list of platform information.
- **dependencies**: A list of dependencies.
- **javaBuildInfo**: Information about the Java build environment.
- **scmInfo**: Information about the source control management.

### Configuration
The extension can be configured using the following application properties:
- quarkus.application.metadata.path: The path to the metadata endpoint. Default: /app-metadata
- quarkus.application.metadata.platformInfo: A list of platform information.
- quarkus.application.metadata.dependencies: A list of dependencies.
- quarkus.application.metadata.javaBuildInfo: Information about the Java build environment.
- quarkus.application.metadata.scmInfo: Information about the source control management.

Configuration property fixed at build time.

## Installation
If you want to use this extension, you need to add the `io.quarkiverse:quarkus-app-metadata-extension` extension first to your build file.

For instance, with Maven, add the following dependency to your POM file:

```xml
<dependency>
    <groupId>io.quarkiverse</groupId>
    <artifactId>quarkus-app-metadata-extension</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Get application metadata via HTTP
Once the extension is added to your project, you can access the application metadata via an HTTP endpoint. By default, the metadata endpoint is `/app-metadata`. You can access the metadata by sending an HTTP GET request to the endpoint (e.g., `http://localhost:8080/app-metadata`). The response will be a JSON object containing the application metadata.

```json
{
  "name": "users-service",
  "groupId": "org.acme",
  "artifactId": "users-service",
  "version": "1.0.0-SNAPSHOT",
  "platformInfo": [
    {
      "platform": "io.quarkus.platform",
      "version": "3.15.1"
    }
  ],
  "dependencies": [
    {
      "groupId": "io.quarkus",
      "artifactId": "quarkus-resteasy",
    }
  ],
  "javaBuildInfo": [
    {
      "javaVendor": "Amazon.com Inc.",
      "javaVendorVersion": "Corretto-21.0.2.13.1",
      "javaVersion": "21.0.2",
      "osArch": "aarch64",
      "osName": "Mac OS X",
      "osVersion": "15.1.1",
      "buildTime": 1733422571929
    }
  ],
  "scmInfo": {
    "branch": "main",
    "tag": null,
    "commitId": "2d41a5c891eb0ee07ba6727265898c4e45c08699",
    "commitDate": "2024-12-05T12:59:31Z",
    "authorName": "Antonio Musarra",
    "authorEmail": "antonio.musarra@gmail.com",
    "remoteUrl": "https://github.com/amusarra/quarkus-users-service.git"
  }
}
```
Console 1 - Example of a JSON response from the metadata endpoint.

### Accessing metadata in your application
You can access the application metadata in your Quarkus application by injecting the `AppMetadataWrapper` bean. The `AppMetadataWrapper` bean provides getter and setter methods to access and modify the metadata.

```java
//...
@Inject
AppMetadataWrapper appMetadataWrapper;

AppMetadata appMetadata = appMetadataWrapper.getAppMetadata();

// Access metadata fields
String name = appMetadata.getName();
String groupId = appMetadata.getGroupId();
String artifactId = appMetadata.getArtifactId();
String version = appMetadata.getVersion();
List<PlatformInfo> platformInfo = appMetadata.getPlatformInfo();
//...
```
Listing 1 - Example of accessing metadata in a Quarkus application via CDI.

## Welcome to Quarkiverse!

Congratulations and thank you for creating a new Quarkus extension project in Quarkiverse!

Feel free to replace this content with the proper description of your new project and necessary instructions how to use and contribute to it.

You can find the basic info, Quarkiverse policies and conventions in [the Quarkiverse wiki](https://github.com/quarkiverse/quarkiverse/wiki).

In case you are creating a Quarkus extension project for the first time, please follow [Building My First Extension](https://quarkus.io/guides/building-my-first-extension) guide.

Other useful articles related to Quarkus extension development can be found under the [Writing Extensions](https://quarkus.io/guides/#writing-extensions) guide category on the [Quarkus.io](https://quarkus.io) website.

Thanks again, good luck and have fun!

## Documentation

The documentation for this extension should be maintained as part of this repository and it is stored in the `docs/` directory.

The layout should follow the [Antora's Standard File and Directory Set](https://docs.antora.org/antora/2.3/standard-directories/).

Once the docs are ready to be published, please open a PR including this repository in the [Quarkiverse Docs Antora playbook](https://github.com/quarkiverse/quarkiverse-docs/blob/main/antora-playbook.yml#L7). See an example [here](https://github.com/quarkiverse/quarkiverse-docs/pull/1)

Your documentation will then be published to the <https://docs.quarkiverse.io/> website.
