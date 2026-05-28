package nlu.fit.web.souvenirecommerce.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import nlu.fit.web.souvenirecommerce.service.RoleInitializationService;
import nlu.fit.web.souvenirecommerce.util.DBContext;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;

@WebListener
public class DbContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HibernateUtil.getSessionFactory();
        System.out.println("Hibernate SessionFactory initialized.");

        // Initialize default roles and permissions
        RoleInitializationService.initializeDefaultRoles();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (HibernateUtil.getSessionFactory() != null){
            HibernateUtil.shutdown();
            System.out.println("Hibernate SessionFactory destroyed.");
        }
        DBContext.shutdown();
        System.out.println("JDBC HikariDataSource destroyed.");
    }
}
