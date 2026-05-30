# RBAC Visual Diagrams & Flow Charts

## 1. System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     User Request                             │
│              (e.g., GET /admin/products)                     │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  HeaderFilter                               │
│  • Set user from session into request attributes            │
│  • Set categories, cart count for UI                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   AuthFilter ⭐                             │
│  • Check if user logged in                                  │
│  • Extract user ID from session                             │
│  • Route to authorization check                             │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│            AuthorizationPolicy.resolve()                    │
│  Maps URL to required permission:                           │
│  • /admin/products GET  → product:read                      │
│  • /admin/products POST → product:create/update/delete      │
│  • /admin/orders GET    → order:read                        │
│  • /admin/roles GET     → role:read                         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│    AuthorizationDAO.hasPermission(userId, resource, action) │
│  Query Database:                                             │
│  SELECT 1 FROM user_roles ur                                │
│  JOIN role_permissions rp ON ur.role_id = rp.role_id        │
│  JOIN permissions p ON rp.permission_id = p.id              │
│  WHERE ur.user_id = ? AND p.resource = ? AND p.action = ?   │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┴────────────────┐
        │                                 │
        ▼                                 ▼
   ✅ ALLOWED                         ❌ DENIED
   Continue to Servlet            Send 403 Forbidden
        │                              │
        └──────────────┬───────────────┘
                       │
                       ▼
          ┌──────────────────────────┐
          │   Servlet Handler        │
          │  (e.g., AdminController) │
          │                          │
          │ Optional: Extra checks   │
          │ PermissionHelper.xxx()   │
          └──────────────┬───────────┘
                         │
                         ▼
          ┌──────────────────────────┐
          │     Response to User      │
          │  • JSP/JSON/Redirect      │
          └──────────────────────────┘
```

---

## 2. Database Schema Diagram

```
┌──────────────────────┐
│       users          │
├──────────────────────┤
│ id (PK)              │
│ email                │
│ password_hash        │
│ full_name            │
│ role (legacy)        │
└──────────────────────┘
         │
         │ (1:N)
         │
         ▼
┌──────────────────────────┐
│    user_roles            │
├──────────────────────────┤
│ user_id (PK, FK)         │ ◄──── One user can have
│ role_id (PK, FK)         │       multiple roles
│ assigned_by              │
│ assigned_at              │
└──────────────────────────┘
         │
         │ (N:1)
         │
         ▼
┌──────────────────────┐
│       roles          │
├──────────────────────┤
│ id (PK)              │
│ name ⭐              │
│ description          │
│ is_system            │
│ created_at           │
└──────────────────────┘
         │
         │ (1:N)
         │
         ▼
┌──────────────────────────┐
│  role_permissions        │
├──────────────────────────┤
│ role_id (PK, FK)         │ ◄──── One role can have
│ permission_id (PK, FK)   │       multiple permissions
└──────────────────────────┘
         │
         │ (N:1)
         │
         ▼
┌──────────────────────────┐
│     permissions          │
├──────────────────────────┤
│ id (PK)                  │
│ resource ⭐              │ ◄─── Examples: product, order, role
│ action ⭐                │ ◄─── Examples: read, create, update, delete
│ description              │
│ created_at               │
│ UNIQUE(resource, action) │
└──────────────────────────┘
```

---

## 3. Permission Resolution Flow

```
URL Request
    │
    ▼
┌─────────────────────────────────────┐
│ AuthorizationPolicy.resolve()       │
│                                     │
│ if "/admin/dashboard" GET           │
│    → RequiredPermission("dashboard", "read", true)
│                                     │
│ if "/admin/products" GET            │
│    → RequiredPermission("product", "read", true)
│                                     │
│ if "/admin/products" POST           │
│    → action = request.getParameter("action")
│    → if action == "delete"          │
│       → RequiredPermission("product", "delete", true)
│    → if action == "create"          │
│       → RequiredPermission("product", "create", true)
│                                     │
└─────────────────────────────────────┘
    │
    ▼
RequiredPermission Object
{
    resource: "product",
    action: "create",
    protectedRoute: true
}
    │
    ▼
hasPermission Check
```

---

## 4. Default Roles Permission Matrix

```
┌────────────┬───────────┬─────────┬──────────┬────────┬──────────┬──────────┬───────┐
│            │ Dashboard │ Product │ Category │ Order  │ Customer │ Banner   │ Role  │
├────────────┼───────────┼─────────┼──────────┼────────┼──────────┼──────────┼───────┤
│ SuperAdmin │ CRUD      │ CRUD    │ CRUD     │ CRUD   │ CRUD     │ CRUD     │ CRUD  │
│            │ ✅        │ ✅      │ ✅       │ ✅     │ ✅       │ ✅       │ ✅    │
├────────────┼───────────┼─────────┼──────────┼────────┼──────────┼──────────┼───────┤
│ Admin      │ CRUD      │ CRUD    │ CRUD     │ CRUD   │ CRUD     │ CRUD     │ ❌    │
│            │ ✅        │ ✅      │ ✅       │ ✅     │ ✅       │ ✅       │       │
├────────────┼───────────┼─────────┼──────────┼────────┼──────────┼──────────┼───────┤
│ Sales      │ R         │ RU      │ ❌       │ RU     │ R        │ ❌       │ ❌    │
│            │ ✅        │ ✅      │          │ ✅     │ ✅       │          │       │
├────────────┼───────────┼─────────┼──────────┼────────┼──────────┼──────────┼───────┤
│ User       │ R         │ ❌      │ ❌       │ ❌     │ ❌       │ ❌       │ ❌    │
│            │ ✅        │          │          │        │          │          │       │
├────────────┼───────────┼─────────┼──────────┼────────┼──────────┼──────────┼───────┤
│ Customer   │ ❌        │ ❌      │ ❌       │ ❌     │ ❌       │ ❌       │ ❌    │
│            │           │          │          │        │          │          │       │
└────────────┴───────────┴─────────┴──────────┴────────┴──────────┴──────────┴───────┘

Legend:
CRUD = Create, Read, Update, Delete (Full access)
R    = Read only
U    = Update only
RU   = Read and Update
❌   = No access
✅   = Has this permission
```

---

## 5. Authorization Check Sequence Diagram

```
User                    AuthFilter           AuthPolicy           AuthzDAO           Database
  │                         │                    │                    │                  │
  ├─ Request /admin/prod ───>                    │                    │                  │
  │                         │                    │                    │                  │
  │                         ├─ Get session user  │                    │                  │
  │                         │                    │                    │                  │
  │                         ├─ resolve() ───────────────────────────>│                  │
  │                         │                    │<─ RequiredPermission
  │                         │                    │                    │                  │
  │                         ├─ hasPermission(userId, "product", "read")─>               │
  │                         │                    │                    │                  │
  │                         │                    │                    ├─ Query users    │
  │                         │                    │                    │  & roles        │
  │                         │                    │                    ├─ Check RolePerms
  │                         │                    │                    │<─ SELECT        │
  │                         │<─ true ────────────────────────────────<─ Yes, has perms  │
  │                         │                    │                    │                  │
  │<─ 200 OK (allowed) ────<│                    │                    │                  │
  │                         │                    │                    │                  │
  ├─ GET /admin/roles ──────>                    │                    │                  │
  │                         │                    │                    │                  │
  │                         ├─ resolve() ───────────────────────────>│                  │
  │                         │                    │<─ RequiredPermission(role, read)      │
  │                         │                    │                    │                  │
  │                         ├─ hasPermission(userId, "role", "read") ──>               │
  │                         │                    │                    │                  │
  │                         │                    │                    ├─ Check perms   │
  │                         │                    │                    │<─ No, no perms   │
  │                         │<─ false ──────────────────────────────<─ Not allowed      │
  │                         │                    │                    │                  │
  │<─ 403 Forbidden ──────<│                    │                    │                  │
  │                         │                    │                    │                  │
```

---

## 6. Permission Check in Code

```java
// In Servlet:
public void doPost(HttpServletRequest request, ...) {
    
    // Auto-checked by AuthFilter
    // If user reaches here, they passed basic check
    
    // Optional: Additional permission checks
    if (PermissionHelper.hasPermission(request, "product", "delete")) {
        // Perform delete operation
        deleteProduct(productId);
    } else {
        // This shouldn't happen if AuthFilter is working
        // But good practice to double-check
        response.sendError(403);
    }
}
```

---

## 7. Role Assignment Flow

```
┌─────────────────────────────────┐
│  Admin wants to assign role     │
│  to user                        │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  Visit /admin/roles             │
│  RequiredPermission: role:read  │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  Select role (e.g., "Sales")    │
│  Select users to assign         │
│  Click "Gán người dùng"         │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  POST /admin/roles              │
│  action=assignUsers             │
│  roleId=2, userIds=[1,3,5]      │
│  RequiredPermission: role:update│
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  AdminRoleController.doPost()   │
│  authDAO.assignUsersToRole()    │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  Insert into user_roles:        │
│  (user_id, role_id) values      │
│  (1, 2), (3, 2), (5, 2)         │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  User #1, #3, #5 now have       │
│  "Sales" role permissions       │
│  Next request → They inherit    │
│  product:read, order:read, etc. │
└─────────────────────────────────┘
```

---

## 8. Permission Check Examples

```
┌─────────────────────────────────────────────────────┐
│ Check 1: Can user delete products?                  │
├─────────────────────────────────────────────────────┤
│ Permission needed: product:delete                   │
│                                                     │
│ SELECT 1                                            │
│ FROM user_roles ur                                  │
│ JOIN role_permissions rp ON ur.role_id=rp.role_id  │
│ JOIN permissions p ON rp.permission_id=p.id        │
│ WHERE ur.user_id = 5                               │
│   AND p.resource = 'product'                        │
│   AND p.action = 'delete'                           │
│                                                     │
│ If result exists → YES, can delete                  │
│ If no result     → NO, cannot delete                │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ Check 2: What permissions does user #5 have?       │
├─────────────────────────────────────────────────────┤
│ SELECT DISTINCT p.resource, p.action                │
│ FROM user_roles ur                                  │
│ JOIN role_permissions rp ON ur.role_id=rp.role_id  │
│ JOIN permissions p ON rp.permission_id=p.id        │
│ WHERE ur.user_id = 5                               │
│                                                     │
│ Result:                                             │
│ product | read                                      │
│ product | update                                    │
│ order   | read                                      │
│ order   | update                                    │
│ customer| read                                      │
│ dashboard| read                                     │
│ (From "Sales" role)                                 │
└─────────────────────────────────────────────────────┘
```

---

## 9. Access Decision Tree

```
                     User Request to /admin/X
                            │
                            ▼
                   Is user logged in?
                    /          \
                  YES           NO
                   │             │
                   │             └──> Redirect to /login
                   │
                   ▼
        Does /admin/X require permission?
             /               \
           YES               NO
            │                │
            │                └──> Allow access
            │
            ▼
    Does AuthorizationPolicy
   map this URL to permission?
        /           \
      YES           NO
       │             │
       │             └──> Allow access (unprotected route)
       │
       ▼
   Get required permission
   (e.g., product:read)
       │
       ▼
   Query: Does user have
   product:read permission?
      /      \
    YES      NO
     │        │
     │        └──> Return 403 Forbidden
     │             "Bạn không có quyền..."
     │
     ▼
   Get from database:
   SELECT ... FROM user_roles ur
   JOIN role_permissions rp
   JOIN permissions p
   WHERE ur.user_id = X
    AND p.resource = 'product'
    AND p.action = 'read'
     │
     ▼
   Result found?
    /      \
  YES      NO
   │        │
   │        └──> DENY (403)
   │
   ▼
  ALLOW ACCESS
  Pass to servlet
```

---

## 10. Initialization Sequence

```
1. Server Starts
   │
   ├─> DbContextListener.contextInitialized()
   │
   └─> HibernateUtil.getSessionFactory() initialized
   
2. Application Initialization
   │
   └─> RoleInitializationService.initializeDefaultRoles()
       │
       ├─> Check: Do 4+ system roles exist?
       │
       ├─> If NO:
       │  ├─> Create Super Admin role
       │  ├─> Create Admin role
       │  ├─> Create Sales role
       │  ├─> Create User role
       │  │
       │  └─> Assign permissions to each role:
       │     ├─> Super Admin ← All 25+ permissions
       │     ├─> Admin ← All except role management
       │     ├─> Sales ← Product, Order, Customer, Dashboard
       │     └─> User ← Dashboard only
       │
       └─> If YES: Skip (already initialized)

3. Application Ready
   │
   └─> AuthFilter active and checking permissions
```

---

These diagrams show the complete flow of how the RBAC system works from multiple perspectives!
