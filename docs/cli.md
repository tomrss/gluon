---
layout: default
title: Command line
nav_order: 2
---

# Gluon Command Line Interface

Gluon can be used via its dedicated command line interface.

Launcher scripts are in the folder `gluon-cli/target/bin`

## Usage
On sh shells:
```shell
./gluon-cli/target/bin/gluon.sh create <options>
```
On Windows:
```
.\gluon-cli\target\bin\gluon.bat create <parameters>
```

In this document, `gluon` alias will be used to identify desired launcher script.

### Help
Get help with:
```shell
gluon --help
```

Help about create command:
```shell
gluon help create
```

### Log level
```shell
gluon --log-level <trace|debug|info|warn|error> create <options>
```

If `--log-level` (short version: `-L`) option is not specified, it defaults to `info`.

### Options

| Option                        | Description                                                                | Domain                                                                        | Default                         |
|-------------------------------|----------------------------------------------------------------------------|-------------------------------------------------------------------------------|---------------------------------|
| `-p`, `--projectArtifactId`   | Name (artifact id) of the project                                          | Artifact id                                                                   | `gluon-example`                 |     
| `-g`, `--projectGroupId`      | Group id of the project, like in Maven                                     | Group id                                                                      | `org.acme`                      |     
| `-v`, `--projectVersion`      | Version of the project                                                     | Version                                                                       | `0.1.0`                         |    
| `-P`, `--projectDirectory`    | Directory of the project                                                   | Filesystem path                                                               | ArtifactId as relative path     |   
| `-n`, `--projectFriendlyName` | Friendly, descriptive name of the project                                  | String                                                                        | Based on artifactId             |
| `-D`, `--projectDescription`  | Project description                                                        | String                                                                        |                                 |
| `-b`, `--basePackage`         | Base package, useful probably only in JVM based projects                   | Java package                                                                  | Based on groupId and artifactId |     
| `-t`, `--customTemplates`     | Path of directory containing custom templates. Cannot use with `archetype` | Filesystem path                                                               |                                 |   
| `-d`, `--databaseVendor`      | Database vendor used in the project                                        | `postgresql`, `db2`, `derby`, `h2`, `mariadb`, `sqlserver`, `mysql`, `oracle` | `postgresql`                    |    
| `-x`, `--templateExtension`   | Extension of template files                                                | File extension                                                                | `.gluon`                        |
| `-e`, `--entities`            | Path of directory containing entity specifications                         | Filesystem path                                                               |                                 |
| `-f`, `--entityFormat`        | Format of entity specifications                                            | `json`, `yaml`, `toml`, `xml`                                                 | `json`                          |   
| `-a`, `--archetype`           | Name of the archetype to use. Cannot use with `customTemplates`            | `quarkus`, `flask`                                                            | `quarkus`                       |
| `-T`, `--projectType`         | Type of project. Cannot use with `archetype`                               | `maven`, `python`                                                             | `maven`                         |
