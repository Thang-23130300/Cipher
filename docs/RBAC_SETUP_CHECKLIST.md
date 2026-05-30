# RBAC System Setup & Migration Checklist

## ✅ Completed Implementation

### Database Changes
- [x] Updated `update_schema.sql` with 4 default roles
  - Super Admin (all permissions)
  - Admin (all except role management)
  - Sales (product, order, customer, dashboard)
  - User (dashboard read-only)
- [x] Added role-permission seeding logic
- [x] Maintained backward compatibility with existing "Admin" and "Customer" roles

### Backend Services
- [x] Created `RoleInitializationService.java`
  - Runs on application startup
  - Creates 4 default roles if they don't exist
  - Assigns correct permissions to each role
  - Idempotent (safe to run multiple times)

- [x] Enhanced `AuthorizationDAO.java` with new methods
  - `getUserPermissions(userId)` - Get all permissions for a user
  - `getUserRoles(userId)` - Get all roles for a user
  - `hasAnyPermission(userId, resources...)` - Check if user has any of several permissions

### Filters & Security
- [x] Fixed and enabled `AuthFilter.java`
  - Now enabled with `@WebFilter(urlPatterns = {"/admin/*", "/user/*"})`
  - Refactored for better readability
  - Proper permission checking without hardcoded role names
  - Added logging for security audits
  - Added helper methods for extracting user from session

### Utilities & Helpers
- [x] Created `PermissionHelper.java` utility class
  - Easy permission checks in servlets
  - Methods: `hasPermission()`, `hasAnyPermission()`, `getUserPermissions()`, etc.
  - Helper methods: `isAdmin()`, `isSuperAdmin()`, `hasProductAccess()`, etc.

- [x] Created `@RequirePermission` annotation
  - Can be used to mark methods/classes as requiring permissions
  - Future enhancement for automated permission checking

### Documentation
- [x] Created comprehensive `RBAC_IMPLEMENTATION_GUIDE.md`
  - Overview of 4 default roles
  - How the system works
  - Implementation details
  - Code examples
  - Troubleshooting guide

- [x] Created `RBAC_QUICK_REFERENCE.md`
  - Quick lookup for permissions
  - Role hierarchy table
  - URL to permission mapping
  - Common errors and solutions

### Integration
- [x] Updated `DbContextListener.java`
  - Calls `RoleInitializationService.initializeDefaultRoles()` on startup
  - Ensures default roles are created automatically

---

## 📋 Setup Instructions

### Step 1: Update Database
```bash
# Run the updated schema migration
mysql -u root -p souvenirDB < update_schema.sql
```

**Or via MySQL client:**
```sql
use souvenirDB;
-- All SQL commands in update_schema.sql will create roles and seed data
```

### Step 2: Rebuild Project
```bash
# Clean and rebuild
mvn clean build

# Or using Maven wrapper
./mvnw clean build
```

### Step 3: Deploy Application
```bash
# Restart your application server (Tomcat, etc.)
# The DbContextListener will automatically initialize roles
```

### Step 4: Verify Setup
1. Check database:
```sql
SELECT * FROM roles;
SELECT COUNT(*) FROM permissions;
SELECT * FROM role_permissions WHERE role_id = 1;
```

2. Check server logs for:
```
INFO: RoleInitializationService - Default roles and permissions initialized successfully!
```

3. Test login with an admin user and access `/admin/dashboard`

---

## 🧪 Testing Checklist

### Test 1: Super Admin Access
```
[ ] Log in as Super Admin user
[ ] Navigate to /admin/dashboard → ✅ Should see
[ ] Navigate to /admin/products → ✅ Should see
[ ] Navigate to /admin/roles → ✅ Should see
[ ] Try to delete a product → ✅ Should succeed
[ ] Try to delete a role → ✅ Should succeed
```

### Test 2: Admin Access (No Role Management)
```
[ ] Log in as Admin user
[ ] Navigate to /admin/dashboard → ✅ Should see
[ ] Navigate to /admin/products → ✅ Should see
[ ] Try to delete a product → ✅ Should succeed
[ ] Navigate to /admin/roles → ❌ Should get 403 Forbidden
[ ] Try to create a role → ❌ Should get 403 Forbidden
```

### Test 3: Sales Access (Limited)
```
[ ] Log in as Sales user
[ ] Navigate to /admin/dashboard → ✅ Should see
[ ] Navigate to /admin/products → ✅ Should see (read/update only)
[ ] Try to create a product → ❌ Should get 403
[ ] Navigate to /admin/orders → ✅ Should see
[ ] Try to delete an order → ❌ Should get 403 (no delete permission)
[ ] Navigate to /admin/categories → ❌ Should get 403
```

### Test 4: User Access (Minimal)
```
[ ] Log in as User role
[ ] Navigate to /admin/dashboard → ✅ Should see
[ ] Navigate to /admin/products → ❌ Should get 403
[ ] Navigate to /admin/orders → ❌ Should get 403
```

### Test 5: Customer Access (None)
```
[ ] Log in as Customer role
[ ] Try to navigate to /admin/dashboard → ❌ Should get 403
[ ] Try to navigate to /admin/products → ❌ Should get 403
```

### Test 6: Dynamic Permission Assignment
```
[ ] Log in as Super Admin
[ ] Go to /admin/roles
[ ] Create a new custom role "Manager" with product and order permissions
[ ] Assign a user to this new role
[ ] Log in as that user
[ ] Verify they can only access products and orders
```

---

## 🔧 Troubleshooting Guide

### Issue: Users get 403 Forbidden on all pages
**Solution:**
1. Check if user has roles assigned:
```sql
SELECT ur.user_id, ur.role_id, r.name 
FROM user_roles ur 
JOIN roles r ON r.id = ur.role_id 
WHERE ur.user_id = ?;
```
2. If no roles, assign one via SQL or admin panel:
```sql
INSERT INTO user_roles (user_id, role_id) 
VALUES (?, 1); -- 1 = Super Admin
```

### Issue: All users can access all pages
**Solution:**
1. Verify AuthFilter is enabled (check `@WebFilter` annotation)
2. Restart application server
3. Check server logs for AuthFilter initialization messages

### Issue: "RoleInitializationService" not found
**Solution:**
1. Ensure file exists: `src/main/java/.../service/RoleInitializationService.java`
2. Check import in `DbContextListener.java`
3. Rebuild project

### Issue: Default roles not created
**Solution:**
1. Manually run SQL:
```sql
INSERT INTO roles (name, description, is_system) VALUES
('Super Admin', 'Full system access', TRUE),
('Admin', 'Admin access', TRUE),
('Sales', 'Sales access', TRUE),
('User', 'Basic user access', TRUE);
```
2. Then run role-permission assignments from `update_schema.sql`

---

## 📊 Database Verification Queries

### Verify Roles Created
```sql
SELECT id, name, is_system FROM roles;
-- Should show: Super Admin, Admin, Sales, User, Customer
```

### Verify Permissions Created
```sql
SELECT resource, action, COUNT(*) as count 
FROM permissions 
GROUP BY resource, action;
-- Should show all resource:action combinations
```

### Verify Super Admin Has All Permissions
```sql
SELECT COUNT(*) as permission_count 
FROM role_permissions 
WHERE role_id = (SELECT id FROM roles WHERE name = 'Super Admin');
-- Should equal total number of permissions
```

### Verify Admin Missing Role Permissions
```sql
SELECT p.resource, p.action 
FROM role_permissions rp 
JOIN permissions p ON p.id = rp.permission_id 
WHERE rp.role_id = (SELECT id FROM roles WHERE name = 'Admin') 
AND p.resource = 'role';
-- Should be empty
```

### Verify Sales Role Permissions
```sql
SELECT p.resource, p.action 
FROM role_permissions rp 
JOIN permissions p ON p.id = rp.permission_id 
WHERE rp.role_id = (SELECT id FROM roles WHERE name = 'Sales') 
ORDER BY p.resource, p.action;
-- Should only have: dashboard:read, product:read, product:update, 
-- order:read, order:update, customer:read
```

### Check User Role Assignments
```sql
SELECT u.id, u.email, GROUP_CONCAT(r.name) as roles 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id 
LEFT JOIN roles r ON r.id = ur.role_id 
GROUP BY u.id, u.email;
```

---

## 🚀 Next Steps

### Optional Enhancements
1. **Create Custom Roles UI** - Better interface for creating/managing custom roles
2. **Permission Audit Logging** - Log all permission checks for security
3. **Cache Permissions** - Cache user permissions in session for performance
4. **JSTL Tag Library** - Create custom tags for permission checks in JSP
5. **API-based Permission System** - REST endpoints for permission management
6. **Bulk Role Assignment** - Assign roles to multiple users at once
7. **Permission Groups** - Group related permissions
8. **Time-based Roles** - Temporary role assignments

### Performance Optimization
1. Cache user permissions in session after first check
2. Use `hasAnyPermission()` for OR checks (fewer DB queries)
3. Add database indexes on frequently queried columns:
```sql
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_role_perms_role_id ON role_permissions(role_id);
```

### Security Hardening
1. Implement permission audit logging
2. Add two-factor authentication
3. Implement rate limiting on failed auth attempts
4. Add permission change notifications
5. Regular permission reviews and cleanup

---

## 📝 Files Modified/Created

### Created Files
- ✅ `src/main/java/nlu/fit/web/souvenirecommerce/service/RoleInitializationService.java`
- ✅ `src/main/java/nlu/fit/web/souvenirecommerce/util/PermissionHelper.java`
- ✅ `src/main/java/nlu/fit/web/souvenirecommerce/annotation/RequirePermission.java`
- ✅ `docs/RBAC_IMPLEMENTATION_GUIDE.md`
- ✅ `docs/RBAC_QUICK_REFERENCE.md`
- ✅ `docs/RBAC_SETUP_CHECKLIST.md` (this file)

### Modified Files
- ✅ `update_schema.sql` - Updated role seeding section
- ✅ `src/main/java/nlu/fit/web/souvenirecommerce/listener/DbContextListener.java`
- ✅ `src/main/java/nlu/fit/web/souvenirecommerce/filter/AuthFilter.java`
- ✅ `src/main/java/nlu/fit/web/souvenirecommerce/dao/AuthorizationDAO.java`

---

## ✨ Summary

The RBAC system is now fully functional with:
- ✅ 4 Default roles with granular permissions
- ✅ Automatic initialization on application startup
- ✅ Permission-based access control on all admin pages
- ✅ Easy-to-use utility classes for permission checks
- ✅ Comprehensive documentation and guides
- ✅ Backward compatible with existing system

Users can now only access admin features that they have explicit permissions for!

---

## 📞 Support

For issues or questions about the RBAC system:
1. Check `RBAC_IMPLEMENTATION_GUIDE.md` for detailed explanations
2. Check `RBAC_QUICK_REFERENCE.md` for quick lookups
3. Review server logs for AuthFilter and RoleInitializationService messages
4. Check database queries in the troubleshooting section above
