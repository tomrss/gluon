# Gluon

![Maven GitHub Action](https://github.com/tomrss/gluon/actions/workflows/maven.yml/badge.svg)

Generate a domain-driven project from definition of domain entities.

Gluon is a scaffolding tool based on template resolution. It provides different sets of opinionated project templates,
called archetypes, but it lets you freedom to provide your own custom templates.

Given a set of domain entities, Gluon will bootstrap the project resolving the templates based on entities.

You can use Gluon as Unix shell script, Windows BAT script or Maven plugin.

Documentation at [https://tomrss.github.io/gluon](https://tomrss.github.io/gluon)

### A note on project name

This project is named "Gluon" after the subatomics suggestions of the Quarkus project. Quarks are subatomic particles, 
and gluons are the gauge bosons of the strong force, practically holding quarks together.

In early stages of development, Gluon could only generate Quarkus projects. Now it can generate any type of Maven 
project but the name remained the same.
