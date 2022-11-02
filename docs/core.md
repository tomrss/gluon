---
layout: default
title: Using core module
nav_order: 10
---

# Using core module

Core module can be used as Maven dependency after [installation](installation.md):

```xml
<dependency>
    <groupId>io.tomrss.gluon</groupId>
    <artifactId>gluon-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

You can obtain a `io.tomrss.gluon.core.Gluon` instance via `io.tomrss.gluon.core.GluonBuilder`.
