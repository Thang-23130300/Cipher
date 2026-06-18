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
            ensureUniqueUserPhone(connection, statement);

            createKeyChangeOtpsTable(connection, statement);
            addOrderAuditReasonColumn(connection, statement);
            reconcileLegacySignatureStatuses(connection, statement);

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

    private static void ensureUniqueUserPhone(Connection connection, Statement statement) throws SQLException {
        if (!tableExists(connection, "users") || !columnExists(connection, "users", "phone")) {
            return;
        }
        if (indexExists(connection, "users", "uk_users_phone")) {
            return;
        }
        if (hasDuplicateUserPhones(connection)) {
            log.warn("Cannot create unique index users.phone because duplicate phone numbers exist. Clean duplicate users.phone values first.");
            return;
        }

        statement.executeUpdate("create unique index uk_users_phone on users (phone)");
        log.info("Created unique index uk_users_phone on users.phone");
    }

    private static boolean hasDuplicateUserPhones(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery("""
                     select phone
                     from users
                     where phone is not null and phone <> ''
                     group by phone
                     having count(*) > 1
                     limit 1
                     """)) {
            return resultSet.next();
        }
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

    private static boolean indexExists(Connection connection, String tableName, String indexName) throws SQLException {
        try (var resultSet = connection.getMetaData().getIndexInfo(connection.getCatalog(), null, tableName, false, false)) {
            while (resultSet.next()) {
                if (indexName.equalsIgnoreCase(resultSet.getString("INDEX_NAME"))) {
                    return true;
                }
            }
            return false;
        }
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }
    private static void createKeyChangeOtpsTable(Connection connection, Statement statement) throws SQLException {
        if (tableExists(connection, "key_change_otps")) {
            return;
        }

        statement.executeUpdate("""
                CREATE TABLE key_change_otps (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    otp_hash VARCHAR(255) NOT NULL,
                    purpose VARCHAR(50) NOT NULL DEFAULT 'KEY_CHANGE',
                    public_key_pending LONGTEXT NOT NULL,
                    expires_at DATETIME NOT NULL,
                    verified_at DATETIME NULL,
                    consumed_at DATETIME NULL,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_key_change_otps_user FOREIGN KEY (user_id) REFERENCES users(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        log.info("Created table key_change_otps for public key update OTPs");
    }

    private static void addOrderAuditReasonColumn(Connection connection, Statement statement) throws SQLException {
        if (!tableExists(connection, "order_audit_logs")
                || columnExists(connection, "order_audit_logs", "reason")) {
            return;
        }
        statement.executeUpdate("alter table order_audit_logs add column reason varchar(500) null after new_value");
        log.info("Added order_audit_logs.reason for order status change history");
    }

    private static void reconcileLegacySignatureStatuses(Connection connection, Statement statement) throws SQLException {
        if (!tableExists(connection, "orders") || !tableExists(connection, "order_status")
                || !columnExists(connection, "orders", "signature_status")) {
            return;
        }

        int repaired = statement.executeUpdate("""
                update orders o
                join order_status current_status on current_status.id = o.status_id
                join order_status next_status on next_status.description = 'Chờ ký xác nhận'
                set o.status_id = next_status.id
                where current_status.description = 'Chờ ký số'
                  and upper(o.signature_status) = 'SIGNED'
                """);
        if (repaired > 0) {
            log.warn("Repaired {} legacy orders from SIGNED/Chờ ký số to Chờ ký xác nhận", repaired);
        }

        try (var resultSet = statement.executeQuery("""
                select count(*)
                from orders o
                join order_status os on os.id = o.status_id
                where os.description = 'Đã hủy'
                  and upper(o.signature_status) = 'SIGNED'
                """)) {
            if (resultSet.next() && resultSet.getLong(1) > 0) {
                log.warn("Found {} SIGNED orders marked Đã hủy. Kept unchanged because cancellation may be intentional; review order_audit_logs.",
                        resultSet.getLong(1));
            }
        }
    }

}
