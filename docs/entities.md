---
layout: default
title: Defining domain entities
nav_order: 2
has_children: true
---

# Defining domain entities

Gluon can bootstrap domain driven projects based on a set of domain entity specification.

Entities can be specified in different formats. Currently supported formats are: JSON, YAML, TOML, XML.

Each entity must be defined in a separate file, all entity files must be put in a directory, known as entity directory.

The project can than be generated using defined entities:

```shell
# using cli
gluon create --entities path/to/entity/folder --entityFormat json

# using maven plugin
mvn io.tomrss.gluon:gluon-maven-plugin:0.1.0:create -Dentities=path/to/entity/folder -DentityFormat=json
```
Default entity format is JSON.

## Entity properties

Work in progress

## Examples

### JSON
Work in progress

### YAML
Work in progress

### TOML
Work in progress

### XML
Work in progress
