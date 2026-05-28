# RBAC Quick Reference

## Check Permissions in Servlet
```java
import nlu.fit.web.souvenirecommerce.util.PermissionHelper;

// Single permission check
if (PermissionHelper.hasPermission(request, "product", "create")) {
    // User can create products
}

// Multiple permissions check
if (PermissionHelper.hasAnyPermission(request, "product", "category")) {
    // User has access to products or categories
}

// Get user ID
long userId = PermissionHelper.getUserId(request);

// Get user's permissions
List<PermissionItem> perms = PermissionHelper.getUserPermissions(request);

// Get user's roles
List<PermissionGroup> roles = PermissionHelper.getUserRoles(request);

// Is super admin?
if (PermissionHelper.isSuperAdmin(request)) {
    // Only Super Admins
}
```

## Role Hierarchy

| Role | Dashboard | Product | Category | Order | Customer | Banner | Settings | Role Mgmt |
|------|-----------|---------|----------|-------|----------|--------|----------|-----------|
| Super Admin | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Admin | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| Sales | ✅ | R/U | ❌ | R/U | R | ❌ | ❌ | ❌ |
| User | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Customer | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |

Legend: ✅ = Full (CRUD), R = Read only, U = Update only, R/U = Read & Update

## Permission Format

```
resource:action

Examples:
- product:read
- product:create
- product:update
- product:delete
- order:update
- dashboard:read
- role:create
```

## Assign Role to User

```java
AuthorizationDAO authDAO = new AuthorizationDAO();

// Get role ID
List<PermissionGroup> roles = authDAO.getAllRoleGroups();
Long salesRoleId = roles.stream()
    .filter(r -> "Sales".equals(r.getName()))
    .map(PermissionGroup::getId)
    .findFirst()
    .orElse(null);

// Assign to users
authDAO.assignUsersToRole(salesRoleId, List.of(userId1, userId2));
```

## Get User Permissions

```java
AuthorizationDAO authDAO = new AuthorizationDAO();

// All user permissions
List<PermissionItem> perms = authDAO.getUserPermissions(userId);

// Check specific permission
boolean canCreate = authDAO.hasPermission(userId, "product", "create");

// User roles
List<PermissionGroup> roles = authDAO.getUserRoles(userId);
```

## Default Resources

```
- dashboard     (read only)
- product       (read, create, update, delete)
- category      (read, create, update, delete)
- order         (read, update only)
- customer      (read, create, update, delete)
- banner        (read, create, update, delete)
- settings      (read, update only)
- role          (read, create, update, delete)
```

## URL to Permission Mapping

| URL | Method | Permission |
|-----|--------|-----------|
| /admin/dashboard | GET | dashboard:read |
| /admin/products | GET | product:read |
| /admin/products | POST (create) | product:create |
| /admin/products | POST (edit) | product:update |
| /admin/products | POST (delete) | product:delete |
| /admin/categories | GET | category:read |
| /admin/categories | POST (create) | category:create |
| /admin/orders | GET | order:read |
| /admin/orders | POST (update) | order:update |
| /admin/customers | GET | customer:read |
| /admin/banners | GET | banner:read |
| /admin/settings | GET | settings:read |
| /admin/settings | POST | settings:update |
| /admin/roles | GET | role:read |
| /admin/roles | POST (create) | role:create |
| /admin/roles | POST (edit) | role:update |
| /admin/roles | POST (delete) | role:delete |

## Control Access in JSP

### With JSTL Custom Tag (if implemented)
```jsp
<c:if test="${user.canAccess('product:create')}">
    <button>Add Product</button>
</c:if>
```

### Using PermissionHelper (Direct)
```jsp
<%@ page import="nlu.fit.web.souvenirecommerce.util.PermissionHelper" %>

<c:if test="${PermissionHelper.hasPermission(pageContext.request, 'product', 'delete')}">
    <button onclick="deleteProduct()">Delete</button>
</c:if>
```

## Troubleshooting Checklist

- [ ] User is logged in
- [ ] User has assigned roles (check `user_roles` table)
- [ ] Role has permissions (check `role_permissions` table)
- [ ] Permission exists (check `permissions` table)
- [ ] AuthFilter is enabled (`@WebFilter` annotation present)
- [ ] RoleInitializationService was called on startup (check logs)
- [ ] Database schema is up to date (check `update_schema.sql`)

## Common Errors

### 403 Forbidden
User lacks required permission. Check:
```java
List<PermissionItem> perms = authDAO.getUserPermissions(userId);
System.out.println("User permissions: " + perms);
```

### User Not Logged In
Session doesn't have user object. Check session attribute names in:
- `HeaderFilter.setAuthUser()`
- `AuthFilter.getSessionUser()`

### Roles Not Created
RoleInitializationService didn't run. Check:
1. Database connection works
2. `DbContextListener` is active
3. Server logs for errors

## Performance Tips

1. Cache user permissions in session
2. Use `hasAnyPermission()` for OR checks (fewer DB queries)
3. Check permissions early in servlet
4. Use role caching for frequently accessed users

## Security Best Practices

1. Always use PermissionHelper instead of skipping checks
2. Deny by default - require explicit permission
3. Check permissions at both filter and servlet level
4. Log all permission denials
5. Regular audit of role assignments
6. Use "is_system" flag to protect default roles
7. Implement proper session timeout
