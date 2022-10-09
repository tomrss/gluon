quarkus.application.name=${artifactId}

quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=gluon_example
quarkus.datasource.password=gluon_example
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/gluon_example

quarkus.liquibase.change-log=liquibase/db-changelog-master.yml
quarkus.liquibase.migrate-at-start=true

quarkus.swagger-ui.always-include=true
