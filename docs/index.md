---
layout: default
title: Home
nav_order: 1
---

# Gluon

Generate a domain-driven project from definition of domain entities.

Gluon is a scaffolding tool based on template resolution. It provides different sets of opinionated project templates,
called archetypes, but it lets you freedom to provide your own custom templates.

Given a set of domain entities, Gluon will bootstrap the project resolving the templates based on entities.

You can use Gluon as Unix shell script, Windows BAT script or Maven plugin.

## Getting started

At the moment, only installation from source is supported.

### Installing from source

```shell
git clone https://github.com/tomrss/gluon
cd gluon
./mvnw clean install
```

This will install core module, Maven plugin and CLI launcher scripts.
