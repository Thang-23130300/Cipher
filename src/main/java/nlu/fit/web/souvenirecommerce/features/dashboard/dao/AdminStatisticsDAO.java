package nlu.fit.web.souvenirecommerce.features.dashboard.dao;

import nlu.fit.web.souvenirecommerce.legacy.utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminStatisticsDAO {

    // 1. Thống kê đơn hàng (Doanh thu / Số lượng) theo ngày
    public Map<String, Double> getOrderStatsByDay(int days, String metric) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT DATE(o.order_date) AS d,
                   COALESCE(SUM(o.total_amount), 0) AS revenue,
                   COUNT(o.id) AS count
            FROM orders o
            JOIN order_status os ON o.status_id = os.id
            WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
              AND (? = 'quantity' OR os.description = 'Hoàn thành')
            GROUP BY d
            ORDER BY d
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            ps.setString(2, metric);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dateStr = rs.getString("d");
                    double val = "value".equalsIgnoreCase(metric) ? rs.getDouble("revenue") : rs.getDouble("count");
                    data.put(dateStr, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Điền các ngày bị thiếu bằng 0
        Map<String, Double> filledData = new LinkedHashMap<>();
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter labelDtf = DateTimeFormatter.ofPattern("dd/MM");

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dbKey = date.format(dtf);
            String labelKey = date.format(labelDtf);
            filledData.put(labelKey, data.getOrDefault(dbKey, 0.0));
        }

        return filledData;
    }

    // 2. Thống kê đơn hàng (Doanh thu / Số lượng) theo tháng
    public Map<String, Double> getOrderStatsByMonth(int months, String metric) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT YEAR(o.order_date) AS y,
                   MONTH(o.order_date) AS m,
                   COALESCE(SUM(o.total_amount), 0) AS revenue,
                   COUNT(o.id) AS count
            FROM orders o
            JOIN order_status os ON o.status_id = os.id
            WHERE o.order_date >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL ? MONTH), '%Y-%m-01')
              AND (? = 'quantity' OR os.description = 'Hoàn thành')
            GROUP BY y, m
            ORDER BY y, m
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, months - 1);
            ps.setString(2, metric);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getInt("y") + "-" + String.format("%02d", rs.getInt("m"));
                    double val = "value".equalsIgnoreCase(metric) ? rs.getDouble("revenue") : rs.getDouble("count");
                    data.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Điền các tháng bị thiếu bằng 0
        Map<String, Double> filledData = new LinkedHashMap<>();
        LocalDate current = LocalDate.now();
        DateTimeFormatter keyDtf = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelDtf = DateTimeFormatter.ofPattern("MM/yy");

        for (int i = months - 1; i >= 0; i--) {
            LocalDate mDate = current.minusMonths(i);
            String dbKey = mDate.format(keyDtf);
            String labelKey = "Tháng " + mDate.getMonthValue();
            filledData.put(labelKey, data.getOrDefault(dbKey, 0.0));
        }

        return filledData;
    }

    // 3. Thống kê số lượng đơn hàng theo Trạng thái
    public Map<String, Double> getOrderStatsByStatus(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT os.description AS status_name, COUNT(o.id) AS total
            FROM orders o
            JOIN order_status os ON o.status_id = os.id
            WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY os.description
            ORDER BY total DESC
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("status_name"), rs.getDouble("total"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 4. Thống kê sản phẩm (Số lượng bán ra)
    public Map<String, Double> getProductSalesQuantity(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT p.name, COALESCE(SUM(od.quantity), 0) AS total_sold
            FROM order_details od
            JOIN products p ON od.product_id = p.id
            JOIN orders o ON od.order_id = o.id
            JOIN order_status os ON o.status_id = os.id
            WHERE os.description = 'Hoàn thành'
              AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY p.id, p.name
            ORDER BY total_sold DESC
            LIMIT 10
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("name"), rs.getDouble("total_sold"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 5. Thống kê sản phẩm (Doanh thu mang lại)
    public Map<String, Double> getProductSalesRevenue(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT p.name, COALESCE(SUM(od.quantity * od.price_at_purchase), 0) AS revenue
            FROM order_details od
            JOIN products p ON od.product_id = p.id
            JOIN orders o ON od.order_id = o.id
            JOIN order_status os ON o.status_id = os.id
            WHERE os.description = 'Hoàn thành'
              AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY p.id, p.name
            ORDER BY revenue DESC
            LIMIT 10
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("name"), rs.getDouble("revenue"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 6. Thống kê sản phẩm theo Mức độ tồn kho (Trạng thái)
    public Map<String, Double> getProductStockStatus() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT 
              SUM(CASE WHEN stock_quantity = 0 THEN 1 ELSE 0 END) AS out_of_stock,
              SUM(CASE WHEN stock_quantity > 0 AND stock_quantity <= 10 THEN 1 ELSE 0 END) AS low_stock,
              SUM(CASE WHEN stock_quantity > 10 THEN 1 ELSE 0 END) AS in_stock
            FROM products
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("Hết hàng", rs.getDouble("out_of_stock"));
                data.put("Sắp hết hàng", rs.getDouble("low_stock"));
                data.put("Còn hàng nhiều", rs.getDouble("in_stock"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 7. Thống kê số khách hàng đăng ký mới theo ngày
    public Map<String, Double> getCustomerRegistrationsByDay(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT DATE(created_at) AS d, COUNT(id) AS count
            FROM users
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY d
            ORDER BY d
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dateStr = rs.getString("d");
                    data.put(dateStr, rs.getDouble("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Điền các ngày bị thiếu bằng 0
        Map<String, Double> filledData = new LinkedHashMap<>();
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter labelDtf = DateTimeFormatter.ofPattern("dd/MM");

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dbKey = date.format(dtf);
            String labelKey = date.format(labelDtf);
            filledData.put(labelKey, data.getOrDefault(dbKey, 0.0));
        }

        return filledData;
    }

    // 8. Thống kê số khách hàng đăng ký mới theo tháng
    public Map<String, Double> getCustomerRegistrationsByMonth(int months) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT YEAR(created_at) AS y,
                   MONTH(created_at) AS m,
                   COUNT(id) AS count
            FROM users
            WHERE created_at >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL ? MONTH), '%Y-%m-01')
            GROUP BY y, m
            ORDER BY y, m
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, months - 1);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getInt("y") + "-" + String.format("%02d", rs.getInt("m"));
                    data.put(key, rs.getDouble("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Điền các tháng bị thiếu bằng 0
        Map<String, Double> filledData = new LinkedHashMap<>();
        LocalDate current = LocalDate.now();
        DateTimeFormatter keyDtf = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = months - 1; i >= 0; i--) {
            LocalDate mDate = current.minusMonths(i);
            String dbKey = mDate.format(keyDtf);
            String labelKey = "Tháng " + mDate.getMonthValue();
            filledData.put(labelKey, data.getOrDefault(dbKey, 0.0));
        }

        return filledData;
    }

    // 9. Thống kê khách hàng theo tổng chi tiêu (Top khách mua nhiều nhất)
    public Map<String, Double> getCustomerSpendings(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT u.full_name, COALESCE(SUM(o.total_amount), 0) AS total_spend
            FROM orders o
            JOIN users u ON o.user_id = u.id
            JOIN order_status os ON o.status_id = os.id
            WHERE os.description = 'Hoàn thành'
              AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY u.id, u.full_name
            ORDER BY total_spend DESC
            LIMIT 10
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("full_name"), rs.getDouble("total_spend"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 10. Thống kê khách hàng theo trạng thái hoạt động (Active vs Inactive)
    public Map<String, Double> getCustomerActiveStatus() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT 
              SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) AS active_users,
              SUM(CASE WHEN is_active = 0 THEN 1 ELSE 0 END) AS inactive_users
            FROM users
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("Đang hoạt động", rs.getDouble("active_users"));
                data.put("Bị khóa / Vô hiệu", rs.getDouble("inactive_users"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 11. Thống kê chữ ký số theo trạng thái xác minh (verify_status)
    public Map<String, Double> getSignatureStatsByStatus(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT verify_status, COUNT(id) AS total
            FROM order_signatures
            WHERE signed_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY verify_status
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("verify_status");
                    String friendlyName = switch (status != null ? status : "UNKNOWN") {
                        case "VALID" -> "Hợp lệ (VALID)";
                        case "INVALID" -> "Không hợp lệ (INVALID)";
                        case "KEY_COMPROMISED_REVIEW" -> "Khóa rò rỉ đang xem xét";
                        default -> "Trạng thái khác";
                    };
                    data.put(friendlyName, rs.getDouble("total"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // 12. Thống kê số đơn hàng đã ký theo ngày
    public Map<String, Double> getSignedOrdersCountByDay(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT DATE(signed_at) AS d, COUNT(id) AS count
            FROM order_signatures
            WHERE signed_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            GROUP BY d
            ORDER BY d
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dateStr = rs.getString("d");
                    data.put(dateStr, rs.getDouble("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Điền các ngày bị thiếu bằng 0
        Map<String, Double> filledData = new LinkedHashMap<>();
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter labelDtf = DateTimeFormatter.ofPattern("dd/MM");

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dbKey = date.format(dtf);
            String labelKey = date.format(labelDtf);
            filledData.put(labelKey, data.getOrDefault(dbKey, 0.0));
        }

        return filledData;
    }

    // 13. Thống kê số đơn hàng đã ký theo tháng
    public Map<String, Double> getSignedOrdersCountByMonth(int months) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT YEAR(signed_at) AS y,
                   MONTH(signed_at) AS m,
                   COUNT(id) AS count
            FROM order_signatures
            WHERE signed_at >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL ? MONTH), '%Y-%m-01')
            GROUP BY y, m
            ORDER BY y, m
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, months - 1);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getInt("y") + "-" + String.format("%02d", rs.getInt("m"));
                    data.put(key, rs.getDouble("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Điền các tháng bị thiếu bằng 0
        Map<String, Double> filledData = new LinkedHashMap<>();
        LocalDate current = LocalDate.now();
        DateTimeFormatter keyDtf = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = months - 1; i >= 0; i--) {
            LocalDate mDate = current.minusMonths(i);
            String dbKey = mDate.format(keyDtf);
            String labelKey = "Tháng " + mDate.getMonthValue();
            filledData.put(labelKey, data.getOrDefault(dbKey, 0.0));
        }

        return filledData;
    }

    // 14. Thống kê giá trị đơn hàng ký hợp lệ vs chưa ký vs không hợp lệ
    public Map<String, Double> getOrderSignatureValueStats(int days) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT 
              COALESCE(SUM(CASE WHEN o.signature_status = 'SIGNED' THEN o.total_amount ELSE 0 END), 0) AS signed_value,
              COALESCE(SUM(CASE WHEN o.signature_status IS NULL OR o.signature_status = 'WAITING_SIGNATURE' THEN o.total_amount ELSE 0 END), 0) AS unsigned_value,
              COALESCE(SUM(CASE WHEN o.signature_status = 'SIGNATURE_INVALID' THEN o.total_amount ELSE 0 END), 0) AS invalid_value
            FROM orders o
            JOIN order_status os ON o.status_id = os.id
            WHERE os.description = 'Hoàn thành'
              AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("Đã ký hợp lệ", rs.getDouble("signed_value"));
                    data.put("Chưa ký / Chờ ký", rs.getDouble("unsigned_value"));
                    data.put("Chữ ký không hợp lệ", rs.getDouble("invalid_value"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
