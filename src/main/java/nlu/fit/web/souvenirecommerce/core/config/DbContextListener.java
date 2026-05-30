package nlu.fit.web.souvenirecommerce.core.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import nlu.fit.web.souvenirecommerce.legacy.utils.DBContext;
import nlu.fit.web.souvenirecommerce.core.config.HibernateUtil;
import org.hibernate.Session;

@WebListener
@Slf4j
public class DbContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String contextPath = sce.getServletContext().getContextPath();
        log.info("Application starting: contextPath={}", contextPath);

        HibernateUtil.getSessionFactory();
        log.info("Hibernate SessionFactory initialized");

        // Verify required roles exist
        verifyRequiredRoles();
        log.info("Application started: contextPath={}", contextPath);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        String contextPath = sce.getServletContext().getContextPath();
        log.info("Application stopping: contextPath={}", contextPath);

        if (HibernateUtil.getSessionFactory() != null){
            HibernateUtil.shutdown();
            log.info("Hibernate SessionFactory destroyed");
        }
        DBContext.shutdown();
        log.info("JDBC HikariDataSource destroyed");
        log.info("Application stopped: contextPath={}", contextPath);
    }

    private void verifyRequiredRoles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long customerRoleCount = session.createQuery(
                    "select count(r.id) from Role r where lower(r.name) = lower(:name)",
                    Long.class)
                    .setParameter("name", "Customer")
                    .uniqueResult();

            if (customerRoleCount == null || customerRoleCount == 0) {
                log.warn("'Customer' role not found. Please run init_auth_roles.sql");
            } else {
                log.info("'Customer' role verified");
            }
        } catch (Exception e) {
            log.error("Error verifying roles", e);
        }
    }
}
