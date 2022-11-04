---
layout: default
title: Maven plugin
nav_order: 3
---

# Maven Plugin

Gluon can be used as a Maven plugin.

## Help
Get help with the `help` goal:

```shell
mvn io.tomrss.gluon:gluon-maven-plugin:0.1.0:help
```

## Create project

The `create` goal creates the target project:

```shell
mvn io.tomrss.gluon:gluon-maven-plugin:0.1.0:create <option>
```

### Options

Options are to be passed in the command line with `-D`, for example:
```shell
mvn io.tomrss.gluon:gluon-maven-plugin:0.1.0:create -DprojectArtifactId=myProject
```

| Option              | Description                                                                | Domain                                                                        | Default                         |
|---------------------|----------------------------------------------------------------------------|-------------------------------------------------------------------------------|---------------------------------|
| projectArtifactId   | Name (artifact id) of the project                                          | Artifact id                                                                   | `gluon-example`                 |     
| projectGroupId      | Group id of the project, like in Maven                                     | Group id                                                                      | `org.acme`                      |     
| projectVersion      | Version of the project                                                     | Version                                                                       | `0.1.0`                         |    
| projectDirectory    | Directory of the project                                                   | Filesystem path                                                               | ArtifactId as relative path     |   
| projectFriendlyName | Friendly, descriptive name of the project                                  | String                                                                        | Based on artifactId             |
| projectDescription  | Project description                                                        | String                                                                        |                                 |
| basePackage         | Base package, useful probably only in JVM based projects                   | Java package                                                                  | Based on groupId and artifactId |     
| customTemplates     | Path of directory containing custom templates. Cannot use with `archetype` | Filesystem path                                                               |                                 |   
| databaseVendor      | Database vendor used in the project                                        | `postgresql`, `db2`, `derby`, `h2`, `mariadb`, `sqlserver`, `mysql`, `oracle` | `postgresql`                    |    
| templateExtension   | Extension of template files                                                | File extension                                                                | `.gluon`                        |
| entities            | Path of directory containing entity specifications                         | Filesystem path                                                               |                                 |
| entityFormat        | Format of entity specifications                                            | `json`, `yaml`, `toml`, `xml`                                                 | `json`                          |   
| archetype           | Name of the archetype to use. Cannot use with `customTemplates`            | `quarkus`, `flask`                                                            | `quarkus`                       |
| projectType         | Type of project. Cannot use with `archetype`                               | `maven`, `python`                                                             | `maven`                         |
