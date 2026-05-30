package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.legacy.model.Promotion;
import nlu.fit.web.souvenirecommerce.legacy.utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionDAO {

    private static final String SELECT_ACTIVE_PROMOTION = """
        SELECT id, product_id, discount_percent, start_date, end_date
        FROM promotions
        WHERE product_id = ?
          AND (start_date IS NULL OR start_date <= NOW())
          AND (end_date IS NULL OR end_date >= NOW())
        ORDER BY discount_percent DESC
        LIMIT 1
    """;

    public Promotion getActivePromotionByProductId(Long productId) {
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTIVE_PROMOTION)) {

            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapPromotion(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Long, Promotion> getActivePromotionsByProductIds(List<Long> productIds) {

        Map<Long, Promotion> map = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) return map;

        StringBuilder sql = new StringBuilder("""
            SELECT p.id, p.product_id, p.discount_percent, p.start_date, p.end_date
            FROM promotions p
            WHERE p.product_id IN (
        """);

        for (int i = 0; i < productIds.size(); i++) {
            sql.append("?");
            if (i < productIds.size() - 1) sql.append(",");
        }

        sql.append("""
            )
            AND (p.start_date IS NULL OR p.start_date <= NOW())
            AND (p.end_date IS NULL OR p.end_date >= NOW())
            ORDER BY p.product_id, p.discount_percent DESC
        """);

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < productIds.size(); i++) {
                ps.setLong(i + 1, productIds.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Long productId = rs.getLong("product_id");
                // Lấy promotion có discount cao nhất cho mỗi product
                map.putIfAbsent(productId, mapPromotion(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private Promotion mapPromotion(ResultSet rs) throws Exception {
        Timestamp start = rs.getTimestamp("start_date");
        Timestamp end   = rs.getTimestamp("end_date");

        return new Promotion(
                rs.getInt("id"),
                rs.getLong("product_id"),
                rs.getInt("discount_percent"),
                start != null ? start.toLocalDateTime() : null,
                end != null ? end.toLocalDateTime() : null
        );
    }
}
