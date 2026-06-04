package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.legacy.model.Review;
import nlu.fit.web.souvenirecommerce.legacy.model.ReviewSummary;
import nlu.fit.web.souvenirecommerce.legacy.utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO {

    public List<Review> getReviewsByProductWithFilter(Long productId, Integer rating, String sort, int offset, int limit) {
        List<Review> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(""" 
                    SELECT r.id,
                           r.product_id,
                           r.user_id,
                           u.full_name AS user_name,
                           r.rating,
                           r.comment,
                           r.created_at
                    FROM reviews r
                    JOIN users u ON r.user_id = u.id
                    WHERE r.product_id = ?
                """);

        if (rating != null) {
            sql.append(" AND r.rating = ? ");
        }

        if ("oldest".equalsIgnoreCase(sort)) {
            sql.append(" ORDER BY r.created_at ASC ");
        } else {
            sql.append(" ORDER BY r.created_at DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setLong(idx++, productId);

            if (rating != null) {
                ps.setInt(idx++, rating);
            }

            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ReviewSummary getReviewSummaryByProductId(Long productId) {
        String sql = """
                    SELECT COUNT(*) AS total_reviews,
                           AVG(rating) AS avg_rating
                    FROM reviews
                    WHERE product_id = ?
                """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total_reviews");
                double avg = rs.getDouble("avg_rating");

                if (rs.wasNull()) {
                    avg = 0.0;
                }

                return new ReviewSummary(total, Math.round(avg * 10.0) / 10.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ReviewSummary(0, 0.0);
    }

    public boolean hasPurchased(int userId, Long productId) {
        String sql = """
                    SELECT 1
                    FROM orders o
                    JOIN order_items oi ON o.id = oi.order_id
                    WHERE o.user_id = ?
                      AND oi.product_id = ?
                    LIMIT 1
                """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setLong(2, productId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> countReviewsByRating(Long productId) {
        Map<String, Integer> map = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            map.put(String.valueOf(i), 0);
        }

        String sql = """
                    SELECT rating, COUNT(*) AS cnt
                    FROM reviews
                    WHERE product_id = ?
                    GROUP BY rating
                """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(String.valueOf(rs.getInt("rating")), rs.getInt("cnt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public boolean addReview(Review r) {
        String sql = """
                    INSERT INTO reviews (product_id, user_id, rating, comment, created_at)
                    VALUES (?, ?, ?, ?, NOW())
                """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, r.getProductId());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getRating());
            ps.setString(4, r.getComment());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mapping ResultSet → Review
    private Review mapReview(ResultSet rs) throws Exception {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setProductId(rs.getLong("product_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setUserName(rs.getString("user_name"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
