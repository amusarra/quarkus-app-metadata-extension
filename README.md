# Quarkus Application Metadata Extension

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse/quarkus-app-metadata-extension?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse/quarkus-app-metadata-extension-parent)

## Overview

The Quarkus Application Metadata Extension provides a way to manage and expose metadata about your Quarkus application. This includes information about the application itself, its dependencies, platform details, Java build information, and source control management (SCM) details.

## Features

- **AppMetadataHandler**: Handles HTTP requests and returns application metadata as a JSON response.
- **AppMetadataWrapper**: A CDI bean that wraps the `AppMetadata` object.
- **AppMetadata**: A record class that holds various metadata about the application, including platform info, dependencies, Java build info, and SCM info.

## Usage

### AppMetadataHandler

The `AppMetadataHandler` class processes `AppMetadata` and returns it as a JSON response. It is typically used in a Quarkus application to expose metadata via an HTTP endpoint.

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
