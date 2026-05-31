package nlu.fit.web.souvenirecommerce.legacy.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nlu.fit.web.souvenirecommerce.common.utils.ApplicationLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public final class DBContext {
    private static final HikariDataSource dataSource;

    static {
        Properties props = ApplicationLoader.getProperties();
        HikariConfig config = new HikariConfig();

        config.setPoolName(props.getProperty("hikari.poolName", "SouvenirJdbcPool"));
        config.setJdbcUrl(required(props, "db.url"));
        config.setUsername(required(props, "db.username"));
        config.setPassword(props.getProperty("db.password", ""));
        config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));

        config.setMaximumPoolSize(intProperty(props, "hikari.maximumPoolSize", 10));
        config.setMinimumIdle(intProperty(props, "hikari.minimumIdle", 2));
        config.setIdleTimeout(longProperty(props, "hikari.idleTimeout", 30000));
        config.setConnectionTimeout(longProperty(props, "hikari.connectionTimeout", 20000));
        config.setMaxLifetime(longProperty(props, "hikari.maxLifetime", 1800000));

        dataSource = new HikariDataSource(config);
    }

    public DBContext() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
        cleanupJdbcDriver();
    }

    private static void cleanupJdbcDriver() {
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getClassLoader() == cl) {
                    DriverManager.deregisterDriver(driver);
                    System.out.println("Deregistered JDBC driver: " + driver);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deregistering JDBC drivers: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            System.out.println("MySQL AbandonedConnectionCleanupThread shut down.");
        } catch (Throwable e) {
            System.err.println("Error shutting down MySQL AbandonedConnectionCleanupThread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Lấy kết nối từ Pool thành công: " + conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }

    private static int intProperty(Properties props, String key, int defaultValue) {
        return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
    }

    private static long longProperty(Properties props, String key, long defaultValue) {
        return Long.parseLong(props.getProperty(key, String.valueOf(defaultValue)));
    }
}
