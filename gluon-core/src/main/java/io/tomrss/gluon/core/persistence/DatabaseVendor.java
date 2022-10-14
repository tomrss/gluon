package io.tomrss.gluon.core.persistence;

import io.tomrss.gluon.core.persistence.impl.*;

import java.util.function.Supplier;

public enum DatabaseVendor {
    DB2(Db2TypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "db2"),
    DERBY(DerbyTypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "derby"),
    H2(H2TypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "h2"),
    MARIADB(MariaDbTypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "mariadb"),
    SQLSERVER(SqlServerTypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "mssql"),
    MYSQL(MySqlTypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "mysql"),
    ORACLE(OracleTypeTranslationStrategy::new, SnakeCaseUpperCaseNamingStrategy::new, "oracle"),
    POSTGRESQL(PostgresTypeTranslationStrategy::new, SnakeCaseNamingStrategy::new, "postgresql"),
    ;

    // TODO we need a way to build jdbc url embedded here

    private final Supplier<SqlTypeTranslationStrategy> sqlTypeTranslationStrategyFactory;
    private final Supplier<PhysicalNamingStrategy> physicalNamingStrategyFactory;
    // TODO gluon is now more powerful than just Quarkus scaffolding, so this should be probably handled in different, more general way
    private final String quarkusDbKind;

    DatabaseVendor(Supplier<SqlTypeTranslationStrategy> sqlTypeTranslationStrategyFactory, Supplier<PhysicalNamingStrategy> physicalNamingStrategyFactory, String quarkusDbKind) {
        this.sqlTypeTranslationStrategyFactory = sqlTypeTranslationStrategyFactory;
        this.physicalNamingStrategyFactory = physicalNamingStrategyFactory;
        this.quarkusDbKind = quarkusDbKind;
    }

    public SqlTypeTranslationStrategy getSqlTypeTranslationStrategy() {
        return sqlTypeTranslationStrategyFactory.get();
    }

    public PhysicalNamingStrategy getPhysicalNamingStrategy() {
        return physicalNamingStrategyFactory.get();
    }

    public String getQuarkusDbKind() {
        return quarkusDbKind;
    }
}
