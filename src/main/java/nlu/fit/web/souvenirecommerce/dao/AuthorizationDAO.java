package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.PermissionGroup;
import nlu.fit.web.souvenirecommerce.model.PermissionItem;
import nlu.fit.web.souvenirecommerce.model.User;
import nlu.fit.web.souvenirecommerce.util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nlu.fit.web.souvenirecommerce.exception.RoleExistsException;
import nlu.fit.web.souvenirecommerce.exception.PermissionNotFoundException;

public class AuthorizationDAO {

    public List<PermissionItem> getAllPermissions() {
        List<PermissionItem> list = new ArrayList<>();
        String sql = "SELECT id, resource, action, description FROM permissions ORDER BY resource, action";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(PermissionItem.builder()
                        .id(rs.getLong("id"))
                        .resource(rs.getString("resource"))
                        .action(rs.getString("action"))
                        .description(rs.getString("description"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PermissionGroup> getAllRoleGroups() {
        List<PermissionGroup> groups = new ArrayList<>();
        String sql = """
                SELECT r.id,
                       r.name,
                       r.description,
                       r.is_system,
                       COUNT(DISTINCT ur.user_id) AS user_count
                FROM roles r
                LEFT JOIN user_roles ur ON ur.role_id = r.id
                GROUP BY r.id, r.name, r.description, r.is_system
                ORDER BY r.is_system DESC, r.name ASC
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Long roleId = rs.getLong("id");
                PermissionGroup group = PermissionGroup.builder()
                        .id(roleId)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .system(rs.getBoolean("is_system"))
                        .userCount(rs.getInt("user_count"))
                        .permissions(getPermissionsByRoleId(roleId))
                        .users(getUsersByRoleId(roleId))
                        .build();
                groups.add(group);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    public PermissionGroup getRoleById(Long roleId) {
        String sql = "SELECT id, name, description, is_system FROM roles WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return PermissionGroup.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .system(rs.getBoolean("is_system"))
                        .permissions(getPermissionsByRoleId(roleId))
                        .users(getUsersByRoleId(roleId))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT permission_id FROM role_permissions WHERE role_id = ? ORDER BY permission_id";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("permission_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<Long> getUserIdsByRoleId(Long roleId) {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT user_id FROM user_roles WHERE role_id = ? ORDER BY user_id";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<PermissionItem> getPermissionsByRoleId(Long roleId) {
        List<PermissionItem> list = new ArrayList<>();
        String sql = """
                SELECT p.id, p.resource, p.action, p.description
                FROM permissions p
                INNER JOIN role_permissions rp ON rp.permission_id = p.id
                WHERE rp.role_id = ?
                ORDER BY p.resource, p.action
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(PermissionItem.builder()
                        .id(rs.getLong("id"))
                        .resource(rs.getString("resource"))
                        .action(rs.getString("action"))
                        .description(rs.getString("description"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getUsersByRoleId(Long roleId) {
        List<User> list = new ArrayList<>();
        String sql = """
                SELECT u.id, u.full_name, u.email, u.phone, u.avatar, u.status, u.role, u.created_at
                FROM users u
                INNER JOIN user_roles ur ON ur.user_id = u.id
                WHERE ur.role_id = ?
                ORDER BY u.full_name ASC
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAvatar(rs.getString("avatar"));
                user.setStatus(rs.getString("status"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveRole(Long roleId, String name, String description, List<Long> permissionIds) {
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            // ensure role name is unique (exclude current role id when updating)
            if (roleNameExists(conn, name, roleId)) {
                throw new RoleExistsException("Role name already exists: " + name);
            }

            Long savedRoleId = roleId != null ? roleId : insertRole(conn, name, description);
            if (savedRoleId <= 0) {
                conn.rollback();
                return false;
            }

            if (roleId != null) {
                if (!updateRoleMeta(conn, roleId, name, description)) {
                    conn.rollback();
                    return false;
                }
            }

            if (!replaceRolePermissions(conn, savedRoleId, permissionIds)) {
                conn.rollback();
                return false;
            }

            // validate permission ids exist (after save to ensure role id available)
            if (permissionIds != null && !permissionIds.isEmpty()) {
                if (!permissionIdsExist(conn, permissionIds)) {
                    conn.rollback();
                    throw new PermissionNotFoundException("One or more permission IDs are invalid");
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean roleNameExists(Connection conn, String name, Long excludeId) throws Exception {
        String sql = "SELECT id FROM roles WHERE name = ?" + (excludeId != null ? " AND id != ?" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeId != null) {
                ps.setLong(2, excludeId);
            }
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private boolean permissionIdsExist(Connection conn, List<Long> ids) throws Exception {
        if (ids == null || ids.isEmpty()) return true;
        // build placeholders
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('?');
        }
        String sql = "SELECT COUNT(*) AS cnt FROM permissions WHERE id IN (" + sb + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            for (Long id : ids) {
                ps.setLong(idx++, id);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long count = rs.getLong("cnt");
                return count == new HashSet<>(ids).size();
            }
        }
        return false;
    }

    public boolean deleteRole(Long roleId) {
        String checkSql = "SELECT is_system FROM roles WHERE id = ?";
        String deleteSql = "DELETE FROM roles WHERE id = ?";

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            boolean isSystem;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setLong(1, roleId);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                isSystem = rs.getBoolean("is_system");
            }

            if (isSystem) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, roleId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean assignUsersToRole(Long roleId, List<Long> userIds) {
        String deleteSql = "DELETE FROM user_roles WHERE role_id = ?";
        String insertSql = "INSERT INTO user_roles (user_id, role_id, assigned_by) VALUES (?, ?, ?)";

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setLong(1, roleId);
                deletePs.executeUpdate();
            }

            if (userIds != null && !userIds.isEmpty()) {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    for (Long userId : userIds) {
                        if (userId == null) {
                            continue;
                        }
                        insertPs.setLong(1, userId);
                        insertPs.setLong(2, roleId);
                        insertPs.setObject(3, null);
                        insertPs.addBatch();
                    }
                    insertPs.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPermission(long userId, String resource, String action) {
        String sql = """
                SELECT 1
                FROM user_roles ur
                INNER JOIN role_permissions rp ON rp.role_id = ur.role_id
                INNER JOIN permissions p ON p.id = rp.permission_id
                WHERE ur.user_id = ? AND p.resource = ? AND p.action = ?
                LIMIT 1
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, resource);
            ps.setString(3, action);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Long insertRole(Connection conn, String name, String description) throws Exception {
        String sql = "INSERT INTO roles (name, description, is_system) VALUES (?, ?, false)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1L;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1L;
    }

    private boolean updateRoleMeta(Connection conn, Long roleId, String name, String description) throws Exception {
        String sql = "UPDATE roles SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setLong(3, roleId);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean replaceRolePermissions(Connection conn, Long roleId, List<Long> permissionIds) throws Exception {
        try (PreparedStatement deletePs = conn.prepareStatement("DELETE FROM role_permissions WHERE role_id = ?")) {
            deletePs.setLong(1, roleId);
            deletePs.executeUpdate();
        }

        if (permissionIds == null || permissionIds.isEmpty()) {
            return true;
        }

        try (PreparedStatement insertPs = conn.prepareStatement(
                "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)")) {
            Set<Long> distinctIds = new HashSet<>(permissionIds);
            for (Long permissionId : distinctIds) {
                if (permissionId == null) {
                    continue;
                }
                insertPs.setLong(1, roleId);
                insertPs.setLong(2, permissionId);
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
        return true;
    }

    public Map<Long, List<Long>> getUserIdsByRoleIds(List<Long> roleIds) {
        Map<Long, List<Long>> result = new LinkedHashMap<>();
        for (Long roleId : roleIds) {
            result.put(roleId, getUserIdsByRoleId(roleId));
        }
        return result;
    }
}
