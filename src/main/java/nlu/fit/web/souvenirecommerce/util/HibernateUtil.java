package nlu.fit.web.souvenirecommerce.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Collection;
import java.util.Properties;

public class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SessionFactory buildSessionFactory(){
        try {
            Configuration configuration = new Configuration().configure();
            applyHikariProperties(configuration);
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

    public static SessionFactory getSessionFactory(){
        return SESSION_FACTORY;
    }

    public static void shutdown(){
        getSessionFactory().close();
    }

    public static Collection<?> getManagedEntities(Session session){
        return session.getManagedEntities();
    }

    private static void applyHikariProperties(Configuration configuration) {
        Properties props = ApplicationLoader.getProperties();

        configuration.setProperty(
                "hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider"
        );
        configuration.setProperty("hibernate.hikari.jdbcUrl", required(props, "db.url"));
        configuration.setProperty("hibernate.hikari.username", required(props, "db.username"));
        configuration.setProperty("hibernate.hikari.password", props.getProperty("db.password", ""));
        configuration.setProperty("hibernate.hikari.driverClassName", props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        configuration.setProperty("hibernate.hikari.poolName", props.getProperty("hibernate.hikari.poolName", "SouvenirHibernatePool"));
        configuration.setProperty("hibernate.hikari.minimumIdle", props.getProperty("hibernate.hikari.minimumIdle", props.getProperty("hikari.minimumIdle", "2")));
        configuration.setProperty("hibernate.hikari.maximumPoolSize", props.getProperty("hibernate.hikari.maximumPoolSize", props.getProperty("hikari.maximumPoolSize", "10")));
        configuration.setProperty("hibernate.hikari.idleTimeout", props.getProperty("hibernate.hikari.idleTimeout", props.getProperty("hikari.idleTimeout", "30000")));
        configuration.setProperty("hibernate.hikari.connectionTimeout", props.getProperty("hibernate.hikari.connectionTimeout", props.getProperty("hikari.connectionTimeout", "20000")));
        configuration.setProperty("hibernate.hikari.maxLifetime", props.getProperty("hibernate.hikari.maxLifetime", props.getProperty("hikari.maxLifetime", "1800000")));
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }
}
