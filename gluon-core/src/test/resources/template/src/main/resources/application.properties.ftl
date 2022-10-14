quarkus.application.name=${project.artifactId}

quarkus.datasource.db-kind=${project.dbVendor.quarkusDbKind}
quarkus.datasource.username=gluon_example
quarkus.datasource.password=gluon_example
# TODO warning: change jdbc url probably valid only for postgres
quarkus.datasource.jdbc.url=jdbc:${project.dbVendor.quarkusDbKind}://localhost:5432/gluon_example
quarkus.hibernate-orm.database.globally-quoted-identifiers=true

quarkus.liquibase.change-log=liquibase/db-changelog-master.yml
quarkus.liquibase.migrate-at-start=true

quarkus.swagger-ui.always-include=true
