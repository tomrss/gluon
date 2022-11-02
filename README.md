# Gluon

![Maven GitHub Action](https://github.com/tomrss/gluon/actions/workflows/maven.yml/badge.svg)

Generate a domain-driven project from definition of domain entities.

Gluon is a code generator based on template resolution. It provides different sets of opinionated project templates,
called archetypes, but it lets you freedom to provide your own custom template.

Given a set of domain entities, Gluon will bootstrap the project resolving the templates based on entities.

You can use Gluon as Unix shell script, Windows BAT script or Maven plugin.

## Installation

### From source

```shell
git clone https://github.com/tomrss/gluon
cd gluon
./mvnw clean install
```

## Usage

Generate project with [Gluon CLI](gluon-cli/README.md) on Linux/MacOS:
```shell
./gluon-cli/target/bin/gluon.sh create -p myproject -P /my/project/dir
```

Generate project with Gluon CLI on Windows:
```shell
.\gluon-cli\target\bin\gluon.bat create -p myproject -P /my/project/dir
```

Generate project with [Gluon Maven Plugin](gluon-maven-plugin/README.md):
```shell
mvn io.tomrss.gluon:gluon-maven-plugin:create -DprojectArtifactId=myproject -DprojectDirectory=/my/project/dir
```

## Writing your own templates

**TODO**

## A note on project name

This project is named "Gluon" after the subatomics suggestions of the Quarkus project. Quarks are subatomic particles, 
and gluons are the gauge bosons of the strong force, practically holding quarks together.

In early stages of development, Gluon could only generate Quarkus projects. Now it can generate any type of Maven 
project but the name remained the same.
