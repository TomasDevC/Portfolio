package com.starterkit.springboot.config;

import java.sql.Types;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.type.StringType;

/**
 * SQLite dialect for Hibernate 5.4.x (Spring Boot 2.3.x).
 * Supports IDENTITY via SQLite's last_insert_rowid().
 */
public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        registerColumnType(Types.BIT, "boolean");
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.INTEGER, "integer");
        // SQLite requires INTEGER PRIMARY KEY for autoincrement identity columns
        registerColumnType(Types.BIGINT, "integer");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.NUMERIC, "numeric");
        registerColumnType(Types.DECIMAL, "decimal");
        registerColumnType(Types.VARCHAR, "varchar");
        registerColumnType(Types.CHAR, "char");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "datetime");

        registerFunction("lower", new StandardSQLFunction("lower", StringType.INSTANCE));
        registerFunction("upper", new StandardSQLFunction("upper", StringType.INSTANCE));
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLiteIdentityColumnSupport();
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        // Avoid adding a second PRIMARY KEY clause when using identity columns
        return "";
    }

    @Override
    public String getAddColumnString() {
        // SQLite supports: ALTER TABLE <table> ADD COLUMN <col> <type>
        return "add column";
    }

    private static class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {
        @Override
        public boolean supportsIdentityColumns() {
            return true;
        }

        @Override
        public String getIdentitySelectString(String table, String column, int type) {
            return "select last_insert_rowid()";
        }

        @Override
        public String getIdentityColumnString(int type) {
            // Hibernate prepends the column type, so only return the identity clause
            return "primary key autoincrement";
        }
    }
}

