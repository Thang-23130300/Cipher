package nlu.fit.web.souvenirecommerce.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.model.User;
import nlu.fit.web.souvenirecommerce.util.AuthorizationPolicy;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Authentication and Authorization Filter
 * Protects admin and user routes by checking if user is logged in
 * and has required permissions for the resource
 */
@WebFilter(urlPatterns = {"/admin/*", "/user/*"})
public class AuthFilter implements Filter {
    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());
    private final AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // Set cache control headers
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // Get user from session
        Object sessionUser = getSessionUser(session);
        
        if (sessionUser == null) {
            logger.warning("Unauthorized access attempt to: " + req.getServletPath());
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Get user ID from session user
        long userId = extractUserId(sessionUser);
        if (userId <= 0) {
            logger.warning("Invalid user ID for: " + req.getServletPath());
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check authorization for admin routes
        if (req.getServletPath().startsWith("/admin")) {
            AuthorizationPolicy.RequiredPermission requiredPermission = AuthorizationPolicy.resolve(req);

            if (requiredPermission.protectedRoute()) {
                // Check if user has the required permission
                boolean hasPermission = authorizationDAO.hasPermission(userId, 
                        requiredPermission.resource(), requiredPermission.action());

                if (!hasPermission) {
                    logger.warning("Access denied for user " + userId + " to " + req.getServletPath() +
                            " - Required: " + requiredPermission.resource() + ":" + requiredPermission.action());
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, 
                            "Bạn không có quyền truy cập chức năng này. (Missing: " + 
                            requiredPermission.resource() + ":" + requiredPermission.action() + ")");
                    return;
                }
                logger.info("Access granted for user " + userId + " to " + req.getServletPath());
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Extract user from session trying multiple attribute names
     */
    private Object getSessionUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("userInSession");
        if (user != null) return user;

        user = session.getAttribute("user");
        if (user != null) return user;

        user = session.getAttribute("currentUser");
        if (user != null) return user;

        user = session.getAttribute("authUser");
        return user;
    }

    /**
     * Extract user ID from either User or Entity User class
     */
    private long extractUserId(Object sessionUser) {
        if (sessionUser == null) {
            return -1;
        }

        // Check for DTO User
        if (sessionUser instanceof User user) {
            return user.getId();
        }

        // Check for Entity User
        if (sessionUser.getClass().getName().contains("entity.User")) {
            try {
                Object id = sessionUser.getClass().getMethod("getId").invoke(sessionUser);
                if (id instanceof Long) {
                    return (Long) id;
                } else if (id instanceof Integer) {
                    return ((Integer) id).longValue();
                }
            } catch (Exception e) {
                logger.severe("Error extracting user ID: " + e.getMessage());
            }
        }

        return -1;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }
}
