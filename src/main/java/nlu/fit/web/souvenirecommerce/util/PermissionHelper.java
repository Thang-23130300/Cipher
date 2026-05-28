package nlu.fit.web.souvenirecommerce.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.model.User;
import nlu.fit.web.souvenirecommerce.model.PermissionGroup;
import nlu.fit.web.souvenirecommerce.model.PermissionItem;

import java.util.List;

/**
 * Utility class for permission checking in servlets.
 * Provides convenient methods to check if a user has specific permissions.
 */
public class PermissionHelper {
    private static final AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    /**
     * Extract user ID from HTTP request
     */
    public static long getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return -1;
        }

        Object user = session.getAttribute("userInSession");
        if (user == null) {
            user = session.getAttribute("user");
        }
        if (user == null) {
            user = session.getAttribute("currentUser");
        }

        return extractUserId(user);
    }

    /**
     * Check if user has specific permission
     */
    public static boolean hasPermission(HttpServletRequest request, String resource, String action) {
        long userId = getUserId(request);
        if (userId <= 0) {
            return false;
        }
        return authorizationDAO.hasPermission(userId, resource, action);
    }

    /**
     * Check if user has any of the specified resource permissions
     */
    public static boolean hasAnyPermission(HttpServletRequest request, String... resources) {
        long userId = getUserId(request);
        if (userId <= 0) {
            return false;
        }
        return authorizationDAO.hasAnyPermission(userId, resources);
    }

    /**
     * Get all permissions for the current user
     */
    public static List<PermissionItem> getUserPermissions(HttpServletRequest request) {
        long userId = getUserId(request);
        if (userId <= 0) {
            return List.of();
        }
        return authorizationDAO.getUserPermissions(userId);
    }

    /**
     * Get all roles assigned to the current user
     */
    public static List<PermissionGroup> getUserRoles(HttpServletRequest request) {
        long userId = getUserId(request);
        if (userId <= 0) {
            return List.of();
        }
        return authorizationDAO.getUserRoles(userId);
    }

    /**
     * Check if user is admin (has admin or super admin role)
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return hasAnyPermission(request, "role");
    }

    /**
     * Check if user is super admin (has all permissions)
     */
    public static boolean isSuperAdmin(HttpServletRequest request) {
        List<PermissionGroup> roles = getUserRoles(request);
        return roles.stream().anyMatch(r -> "Super Admin".equalsIgnoreCase(r.getName()));
    }

    /**
     * Check if user has any product-related permissions
     */
    public static boolean hasProductAccess(HttpServletRequest request) {
        return hasAnyPermission(request, "product", "category");
    }

    /**
     * Check if user has any order-related permissions
     */
    public static boolean hasOrderAccess(HttpServletRequest request) {
        return hasPermission(request, "order", "read");
    }

    /**
     * Check if user has any customer-related permissions
     */
    public static boolean hasCustomerAccess(HttpServletRequest request) {
        return hasAnyPermission(request, "customer");
    }

    /**
     * Extract user ID from either User DTO or Entity
     */
    private static long extractUserId(Object user) {
        if (user == null) {
            return -1;
        }

        // Check for DTO User
        if (user instanceof User userDto) {
            return userDto.getId();
        }

        // Check for Entity User
        if (user.getClass().getName().contains("entity.User")) {
            try {
                Object id = user.getClass().getMethod("getId").invoke(user);
                if (id instanceof Long) {
                    return (Long) id;
                } else if (id instanceof Integer) {
                    return ((Integer) id).longValue();
                }
            } catch (Exception e) {
                // Ignore and return -1
            }
        }

        return -1;
    }
}
