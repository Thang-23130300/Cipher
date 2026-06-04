package nlu.fit.web.souvenirecommerce.core.config;

import lombok.extern.slf4j.Slf4j;
import nlu.fit.web.souvenirecommerce.common.utils.ApplicationLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
public final class SchemaMigrationRunner {

    private SchemaMigrationRunner() {
    }

    public static void runBeforeHibernate() {
        Properties props = ApplicationLoader.getProperties();
        String url = required(props, "db.url");
        String username = required(props, "db.username");
        String password = props.getProperty("db.password", "");

        try {
            Class.forName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Database driver not found", e);
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            migrateUserPasswordColumn(connection, statement);
        } catch (SQLException e) {
            throw new IllegalStateException("Database schema migration failed", e);
        }
    }

    private static void migrateUserPasswordColumn(Connection connection, Statement statement) throws SQLException {
        if (!tableExists(connection, "users") || !columnExists(connection, "users", "password")) {
            return;
        }

        if (tableExists(connection, "user_credentials")) {
            int migrated = statement.executeUpdate("""
                    insert into user_credentials (user_id, password_hash, email_verified, created_at, updated_at)
                    select u.id, u.password, true, current_timestamp(6), current_timestamp(6)
                    from users u
                    left join user_credentials uc on uc.user_id = u.id
                    where uc.user_id is null
                      and u.password is not null
                      and u.password <> ''
                    """);
            log.info("Migrated {} legacy users.password values to user_credentials.password_hash", migrated);
        }

        statement.executeUpdate("alter table users drop column password");
        log.info("Dropped legacy users.password column");
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (var resultSet = connection.getMetaData().getTables(connection.getCatalog(), null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        try (var resultSet = connection.getMetaData().getColumns(connection.getCatalog(), null, tableName, columnName)) {
            return resultSet.next();
        }
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }
}
