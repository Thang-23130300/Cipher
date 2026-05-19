package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.model.Address;
import nlu.fit.web.souvenirecommerce.model.User;
import nlu.fit.web.souvenirecommerce.util.DBContext;
import nlu.fit.web.souvenirecommerce.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl {

    public User login(String loginDetail, String password) {
        String sql = "SELECT * FROM users WHERE (email = ? OR phone = ?) AND status = 'Active'";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loginDetail.trim());
            ps.setString(2, loginDetail.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next() && PasswordUtil.checkPassword(password, rs.getString("password"))) {
                return mapUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String email, String password, String fullName, String phone) {
        String sql = "INSERT INTO users (full_name, email, password, phone, status, role, avatar) " +
                "VALUES (?, ?, ?, ?, 'Active', 'User', 'default-avatar.png')";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hashPassword(password));
            ps.setString(4, phone);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setResetCode(String accountInfo, String code) {
        String sql = "UPDATE users SET reset_token = ?, token_expiry = DATE_ADD(NOW(), INTERVAL 5 MINUTE) " +
                "WHERE email = ? OR phone = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, accountInfo.trim());
            ps.setString(3, accountInfo.trim());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyCode(String accountInfo, String code) {
        String sql = "SELECT id FROM users WHERE (email = ? OR phone = ?) " +
                "AND reset_token = ? AND token_expiry > NOW()";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountInfo.trim());
            ps.setString(2, accountInfo.trim());
            ps.setString(3, code);
            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean resetPassword(String accountInfo, String newPassword) {
        String sql = "UPDATE users SET password = ?, reset_token = NULL, token_expiry = NULL " +
                "WHERE email = ? OR phone = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setString(2, accountInfo.trim());
            ps.setString(3, accountInfo.trim());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkPassword(int userId, String rawPassword) {
        String sql = "SELECT password FROM users WHERE id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && PasswordUtil.checkPassword(rawPassword, rs.getString("password"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePasswordByUserId(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProfile(int userId, String fullName, String phone, String gender, String dob) {
        try (Connection conn = new DBContext().getConnection()) {
            boolean hasGender = hasColumn(conn, "users", "gender");
            boolean hasDob = hasColumn(conn, "users", "dob");
            StringBuilder sql = new StringBuilder("UPDATE users SET full_name = ?, phone = ?");
            if (hasGender) {
                sql.append(", gender = ?");
            }
            if (hasDob) {
                sql.append(", dob = ?");
            }
            sql.append(" WHERE id = ?");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int index = 1;
                ps.setString(index++, normalize(fullName));
                ps.setString(index++, normalize(phone));
                if (hasGender) {
                    ps.setString(index++, normalize(gender));
                }
                if (hasDob) {
                    ps.setString(index++, normalize(dob));
                }
                ps.setInt(index, userId);
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addAddress(int userId, String detail, String city, String district, String ward) {

        if (detail == null || detail.isBlank()) return false;

        try (Connection conn = new DBContext().getConnection()) {
            boolean hasDefault = hasColumn(conn, "addresses", "is_default");
            String checkSql = "SELECT COUNT(*) FROM addresses WHERE user_id = ?";
            String insertSql = hasDefault
                    ? "INSERT INTO addresses (user_id, address_detail, city, district, ward, is_default) VALUES (?, ?, ?, ?, ?, ?)"
                    : "INSERT INTO addresses (user_id, address_detail, city, district, ward) VALUES (?, ?, ?, ?, ?)";

            boolean isFirst = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    isFirst = true;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, userId);
                ps.setString(2, detail);
                ps.setString(3, city);
                ps.setString(4, district);
                ps.setString(5, ward);
                if (hasDefault) {
                    ps.setInt(6, isFirst ? 1 : 0);
                }
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Address getAddressById(int id) {
        String sql = "SELECT * FROM addresses WHERE id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Address addr = new Address();
                addr.setId(rs.getInt("id"));
                addr.setUserId(rs.getInt("user_id"));
                addr.setAddressDetail(rs.getString("address_detail"));
                addr.setWard(rs.getString("ward"));
                addr.setDistrict(rs.getString("district"));
                addr.setCity(rs.getString("city"));
                addr.setIsDefault(hasColumn(rs, "is_default") ? rs.getInt("is_default") : 0);
                return addr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean deleteAddress(int addressId, int userId) {
        String sql = "DELETE FROM addresses WHERE id = ? AND user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Address> getAddressesByUserId(int userId) {
        List<Address> list = new ArrayList<>();

        String sql = "SELECT * FROM addresses WHERE user_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address a = new Address();
                a.setId(rs.getInt("id"));
                a.setUserId(rs.getInt("user_id"));
                a.setAddressDetail(rs.getString("address_detail"));
                a.setWard(rs.getString("ward"));
                a.setDistrict(rs.getString("district"));
                a.setCity(rs.getString("city"));
                a.setIsDefault(hasColumn(rs, "is_default") ? rs.getInt("is_default") : 0);
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public boolean updateAddress(
            int addressId,
            int userId,
            String detail,
            String ward,
            String district,
            String city
    ) {
        String sql = "UPDATE addresses " +
                "SET address_detail = ?, ward = ?, district = ?, city = ? " +
                "WHERE id = ? AND user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, detail);
            ps.setString(2, ward);
            ps.setString(3, district);
            ps.setString(4, city);
            ps.setInt(5, addressId);
            ps.setInt(6, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAddress(int addressId) {
        String sql = "DELETE FROM addresses WHERE id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setDefaultAddress(int userId, int addressId) {
        String resetSql = "UPDATE addresses SET is_default = 0 WHERE user_id = ?";
        String setSql   = "UPDATE addresses SET is_default = 1 WHERE id = ? AND user_id = ?";

        try (Connection conn = new DBContext().getConnection()) {
            if (!hasColumn(conn, "addresses", "is_default")) {
                return;
            }
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(resetSql);
                 PreparedStatement ps2 = conn.prepareStatement(setSql)) {

                ps1.setInt(1, userId);
                ps1.executeUpdate();

                ps2.setInt(1, addressId);
                ps2.setInt(2, userId);
                ps2.executeUpdate();

                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalCustomers() {
        String sql = "SELECT COUNT(*) as total FROM users WHERE role = 'User'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<User> getAllCustomers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'User' ORDER BY id DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getCustomersWithPagination(int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'User' ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertUser(String fullName, String email, String password, String phone) {
        String sql = "INSERT INTO users (full_name, email, password, phone, status, role, avatar) " +
                "VALUES (?, ?, ?, ?, 'Active', 'User', 'default-avatar.png')";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hashPassword(password));
            ps.setString(4, phone);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(int userId, String fullName, String email, String phone) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateAvatar(int userId, String avatar) {
        String sql = "UPDATE users SET avatar = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, avatar);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAvatar(rs.getString("avatar"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getString("created_at"));
        if (hasColumn(rs, "gender")) {
            user.setGender(rs.getString("gender"));
        }
        if (hasColumn(rs, "dob")) {
            user.setDob(rs.getString("dob"));
        }
        return user;
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, columnName)) {
                if (rs.next()) {
                    return true;
                }
            }
            try (ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName.toUpperCase(), columnName)) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    //check
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra email đã tồn tại chưa
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
