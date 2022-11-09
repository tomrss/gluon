---
layout: default
title: Defining domain entities
nav_order: 4
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

## Entity specification

The meaning of the following properties can be interpreted differently in different sets of templates.
In template sets provided by default ([archetypes](archetypes)), they tend to have the same meaning when it's possible.

**NOTE** - Defaults are not handled yet: specify every property described below!
### Entity
* `name` - Name of the entity. When using an ORM, it should match class name.
* `fields` - A list of specifications of [field](#field).
* `relations` - A list of specifications of [relation](#relation).
* `indexes` - A list of specifications of [index](#index).

### Field
* `name` - Name of the field. Should match name of field in class.
* `type` - Type of the field (for example, Java type of field).
* `nullable` - Whether the field is nullable (boolean).
* `unique` - Whether the field is unique (boolean).
* `length` - Length of the field if applicable (integer).

### Relation
* `name` - Name of the relation (for example, class field of relation field in ORM).
* `targetEntity` - Name of the [entity](#entity) that is target of the relation.
* `type` - Type of relation. Can be one of: `ONE_TO_ONE`, `MANY_TO_ONE`, `ONE_TO_MANY`, `MANY_TO_MANY`.

### Index
* `name` - Name of the index.
* `fields` - A list of names of [fields](#field) that compose the index.
* `unique` - Whether the index is a unique index (boolean). Defaults to false.

## Examples

### JSON

`User.json`:
```json
{
  "name": "User",
  "fields": [
    {
      "name": "name",
      "type": "java.lang.String",
      "nullable": false,
      "unique": true,
      "length": 50
    },
    {
      "name": "email",
      "type": "java.lang.String",
      "nullable": false,
      "unique": true,
      "length": 255
    },
    {
      "name": "enabled",
      "type": "java.lang.Boolean",
      "nullable": true,
      "unique": false
    }
  ],
  "relations": [
    {
      "name": "userSets",
      "targetEntity": "UserSet",
      "type": "MANY_TO_MANY"
    },
    {
      "name": "role",
      "targetEntity": "Role",
      "type": "MANY_TO_ONE"
    }
  ],
  "indexes": [
    {
      "name": "username_email",
      "fields": [
        "name",
        "email"
      ],
      "unique": true
    }
  ]
}
```

`Role.json`:
```json
{
  "name": "Role",
  "fields": [
    {
      "name": "code",
      "type": "java.lang.String",
      "nullable": false,
      "unique": true,
      "length": 50
    },
    {
      "name": "enabled",
      "type": "java.lang.Boolean",
      "nullable": true,
      "unique": false
    }
  ],
  "relations": [
    {
      "name": "capabilities",
      "targetEntity": "Capability",
      "type": "MANY_TO_MANY"
    }
  ],
  "indexes": []
}
```

`Capability.json`:
```json
{
  "name": "Capability",
  "fields": [
    {
      "name": "code",
      "type": "java.lang.String",
      "nullable": false,
      "unique": true,
      "length": 150
    },
    {
      "name": "enabled",
      "type": "java.lang.Boolean",
      "nullable": true,
      "unique": false
    },
    {
      "name": "assignable",
      "type": "java.lang.Boolean",
      "nullable": true,
      "unique": false
    }
  ],
  "relations": [],
  "indexes": []
}
```

`UserSet.json`:
```json
{
  "name": "UserSet",
  "fields": [
    {
      "name": "code",
      "type": "java.lang.String",
      "nullable": false,
      "unique": true,
      "length": 100
    },
    {
      "name": "enabled",
      "type": "java.lang.Boolean",
      "nullable": true,
      "unique": false
    },
    {
      "name": "assignable",
      "type": "java.lang.Boolean",
      "nullable": true,
      "unique": false
    }
  ],
  "relations": [],
  "indexes": []
}
```
### YAML

Work in progress

### TOML

Work in progress

### XML

Work in progress
