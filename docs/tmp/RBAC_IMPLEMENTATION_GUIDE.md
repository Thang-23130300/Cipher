# Role-Based Access Control (RBAC) Implementation Guide

## Overview
This guide explains the RBAC system implemented in the Souvenir E-commerce application. The system provides fine-grained permission management for admin features based on roles.

## Default Roles

### 1. **Super Admin**
- **Description**: Full system access including role management
- **Permissions**: All permissions (complete access)
- **Use Case**: System administrators who manage everything including roles and permissions

### 2. **Admin**
- **Description**: Administrative access to all features except role management
- **Permissions**: 
  - Dashboard: read
  - Product: read, create, update, delete
  - Category: read, create, update, delete
  - Order: read, update
  - Customer: read, create, update, delete
  - Banner: read, create, update, delete
  - Settings: read, update
- **Use Case**: Administrators who manage products, orders, customers, but cannot manage roles

### 3. **Sales**
- **Description**: Sales management access (products, orders, customers)
- **Permissions**:
  - Dashboard: read
  - Product: read, update
  - Order: read, update
  - Customer: read
- **Use Case**: Sales representatives who view and update orders and products but cannot create/delete

### 4. **User**
- **Description**: Basic user access
- **Permissions**:
  - Dashboard: read only
- **Use Case**: Regular users with minimal access

### 5. **Customer**
- **Description**: Default customer account for non-admin users
- **Permissions**: None (customer-level access only)
- **Use Case**: Regular shop customers

## How It Works

### Database Schema
```
Relationships:
users (1) ---> (many) user_roles (many) ---> (1) roles
roles (1) ---> (many) role_permissions (many) ---> (1) permissions
```

### Permission Structure
- **Resource**: What feature (product, order, customer, etc.)
- **Action**: What action (read, create, update, delete)
- **Full Permission**: resource + action (e.g., "product:create")

## Implementation Details

### 1. AuthFilter - Central Authorization Check
The `AuthFilter` intercepts all admin and user requests:
```
Request -> AuthFilter -> AuthorizationPolicy.resolve() -> hasPermission() -> Allow/Deny
```

**Location**: `filter/AuthFilter.java`
- Enabled on: `/admin/*` and `/user/*` routes
- Checks if user has required permission
- Returns 403 Forbidden if not authorized

### 2. AuthorizationPolicy - Permission Resolution
Maps URL patterns to required permissions:

**Location**: `util/AuthorizationPolicy.java`
- `/admin/dashboard` → `dashboard:read`
- `/admin/products` → `product:read` (GET), `product:create/update/delete` (POST)
- `/admin/categories` → `category:read` (GET), etc.
- `/admin/orders` → `order:read` (GET), `order:update` (POST)
- `/admin/customers` → `customer:read` (GET), etc.
- `/admin/banners` → `banner:read` (GET), etc.
- `/admin/settings` → `settings:read` (GET), `settings:update` (POST)
- `/admin/roles` → `role:read/create/update/delete`

### 3. RoleInitializationService - Automatic Setup
Runs on application startup and creates the 4 default roles if they don't exist.

**Location**: `service/RoleInitializationService.java`
- Called from `DbContextListener` during application initialization
- Idempotent (safe to run multiple times)
- Sets up all default role-permission mappings

## Using the System

### In Servlets

#### Check User Permissions
```java
import nlu.fit.web.souvenirecommerce.util.PermissionHelper;

// Check single permission
if (PermissionHelper.hasPermission(request, "product", "create")) {
    // User can create products
}

// Check multiple permissions
if (PermissionHelper.hasAnyPermission(request, "product", "category")) {
    // User can access product or category features
}

// Get user's roles
List<PermissionGroup> userRoles = PermissionHelper.getUserRoles(request);

// Get user's permissions
List<PermissionItem> permissions = PermissionHelper.getUserPermissions(request);

// Check if user is admin
if (PermissionHelper.isAdmin(request)) {
    // Admin-only logic
}

// Check if user is super admin
if (PermissionHelper.isSuperAdmin(request)) {
    // Super admin-only logic
}
```

#### Assign Roles to Users
```java
import nlu.fit.web.souvenirecommerce.dao.AuthorizationDAO;

AuthorizationDAO authDAO = new AuthorizationDAO();
List<Long> userIds = List.of(userId1, userId2);

// Assign "Sales" role to users
authDAO.assignUsersToRole(salesRoleId, userIds);
```

### In JSP Views

#### Show Content Based on Permissions
```jsp
<%@ page import="nlu.fit.web.souvenirecommerce.util.PermissionHelper" %>

<c:if test="${PermissionHelper.hasPermission(pageContext.request, 'product', 'create')}">
    <!-- Show create product button -->
    <button>Add Product</button>
</c:if>

<c:if test="${PermissionHelper.hasPermission(pageContext.request, 'product', 'delete')}">
    <!-- Show delete button -->
    <button onclick="deleteProduct()">Delete</button>
</c:if>
```

Or using JSTL:
```jsp
<c:set var="userId" value="${userInSession.id}" />
<authz:hasPermission userId="${userId}" resource="product" action="create">
    <button>Add Product</button>
</authz:hasPermission>
```

## Admin Panel Navigation

### Role Management Page (`/admin/roles`)
- **Required Permission**: `role:read` to view the page
- **Create Role**: `role:create`
- **Edit Role**: `role:update`
- **Delete Role**: `role:delete`
- **Assign Permissions**: `role:update`
- **Assign Users**: `role:update`

### Example Scenarios

#### Super Admin
- Can access ALL admin pages
- Can manage roles and permissions
- Can assign users to roles
- Can create/edit/delete any resource

#### Admin
- Can access product, category, order, customer, banner, settings pages
- Cannot access role management
- Can do all CRUD operations on products, categories, etc.
- Can read and update orders

#### Sales User
- Can only see dashboard
- Can view and update orders
- Can view and update products (read/update only)
- Can view customers (read only)
- Cannot create/delete products

#### Regular User
- Can only see dashboard
- Cannot access any other admin features

## Database Queries

### Get User Permissions
```sql
SELECT DISTINCT p.resource, p.action
FROM user_roles ur
INNER JOIN role_permissions rp ON rp.role_id = ur.role_id
INNER JOIN permissions p ON p.id = rp.permission_id
WHERE ur.user_id = ?;
```

### Get User Roles
```sql
SELECT r.name, r.description
FROM user_roles ur
INNER JOIN roles r ON r.id = ur.role_id
WHERE ur.user_id = ?;
```

### Check if User Has Permission
```sql
SELECT 1
FROM user_roles ur
INNER JOIN role_permissions rp ON rp.role_id = ur.role_id
INNER JOIN permissions p ON p.id = rp.permission_id
WHERE ur.user_id = ? AND p.resource = ? AND p.action = ?
LIMIT 1;
```

## Adding New Permissions

### Step 1: Add Permission to Database
```sql
INSERT INTO permissions (resource, action, description)
VALUES ('new_feature', 'read', 'View new feature');
```

### Step 2: Update AuthorizationPolicy (Optional)
If you need custom URL-to-permission mapping:
```java
if ("/admin/new-feature".equals(servletPath)) {
    return resolveCrudPermission(request, "new_feature", "read");
}
```

### Step 3: Use Permission in Code
```java
if (PermissionHelper.hasPermission(request, "new_feature", "read")) {
    // Allow access
}
```

## Adding New Roles

### Step 1: Create Role in Database
```sql
INSERT INTO roles (name, description, is_system)
VALUES ('Content Manager', 'Can manage products and banners', FALSE);
```

### Step 2: Assign Permissions to Role
```java
List<Long> permissionIds = authDAO.getPermissionIdsByResource("product", "banner");
authDAO.saveRole(roleId, "Content Manager", description, permissionIds);
```

### Or via SQL:
```sql
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'Content Manager'
  AND p.resource IN ('product', 'banner')
  AND p.action IN ('read', 'update');
```

### Step 3: Assign Role to Users
```java
authDAO.assignUsersToRole(roleId, List.of(userId1, userId2));
```

## Testing Permissions

### Test 1: Admin Can Access Product Page
1. Log in as user with "Admin" role
2. Navigate to `/admin/products`
3. Should see product list and can perform all operations

### Test 2: Sales User Cannot Delete Products
1. Log in as user with "Sales" role
2. Navigate to `/admin/products`
3. Should see product list
4. Delete button should NOT appear (or should be disabled)

### Test 3: User Cannot Access Order Page
1. Log in as user with "User" role
2. Try to access `/admin/orders`
3. Should get 403 Forbidden error

### Test 4: Super Admin Can Manage Roles
1. Log in as user with "Super Admin" role
2. Navigate to `/admin/roles`
3. Should be able to create, edit, delete roles

## Troubleshooting

### User Gets 403 Forbidden
**Check:**
1. User is logged in: `HttpSession` has user object
2. User has assigned roles: Check `user_roles` table
3. Role has permissions: Check `role_permissions` table
4. Permission exists: Check `permissions` table

**Debug:**
```java
// In servlet
long userId = PermissionHelper.getUserId(request);
List<PermissionGroup> roles = PermissionHelper.getUserRoles(request);
List<PermissionItem> permissions = PermissionHelper.getUserPermissions(request);

System.out.println("User ID: " + userId);
System.out.println("Roles: " + roles);
System.out.println("Permissions: " + permissions);
```

### Roles Not Loading on Startup
**Check:**
1. Database is running
2. `update_schema.sql` has been executed
3. `DbContextListener` is being called (check server logs)
4. Database connection is working

## Best Practices

1. **Always use PermissionHelper** for permission checks instead of direct DAO calls
2. **Use AuthorizationPolicy** for URL-to-permission mapping
3. **Keep permissions granular** - use specific resources and actions
4. **Make roles system** - prevents accidental deletion
5. **Log permission denials** - helps with auditing and debugging
6. **Test all role scenarios** before deployment
7. **Document custom permissions** if adding new ones
8. **Review user roles** periodically to ensure least privilege

## Architecture Diagram

```
User Request
    ↓
HeaderFilter (Set user in request)
    ↓
AuthFilter (Check authentication)
    ↓
AuthFilter (Check authorization)
    ├─ AuthorizationPolicy.resolve() → Get required permission
    ├─ AuthorizationDAO.hasPermission() → Check permission
    └─ If not allowed → 403 Forbidden
    ↓
Servlet Handler
    ↓
PermissionHelper.hasPermission() → Optional: Additional checks
    ↓
Response
```

## Files Involved

- **Filters**: `filter/AuthFilter.java`, `filter/HeaderFilter.java`
- **DAOs**: `dao/AuthorizationDAO.java`
- **Services**: `service/RoleInitializationService.java`
- **Utilities**: `util/AuthorizationPolicy.java`, `util/PermissionHelper.java`
- **Annotations**: `annotation/RequirePermission.java`
- **Models**: `model/PermissionGroup.java`, `model/PermissionItem.java`, `model/entity/Role.java`, `model/entity/Permission.java`
- **Controllers**: `controller/admin/AdminRoleController.java`
- **Database**: `update_schema.sql` (schema and seed data)
- **Listeners**: `listener/DbContextListener.java`

## References

- [OWASP Authorization Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html)
- [Spring Security Role-Based Access Control](https://spring.io/projects/spring-security)
