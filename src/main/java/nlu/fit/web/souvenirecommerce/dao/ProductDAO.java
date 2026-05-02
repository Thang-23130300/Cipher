package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.Enums.ProductSort;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String SEARCH_SELECT = """
    SELECT
        p.id,
        p.category_id,
        p.name,
        p.description,
        p.original_price,
        p.image_url,
        p.stock_quantity,
        p.total_sold,

        COALESCE(ROUND(AVG(r.rating), 1), 0) AS avg_rating,
        COUNT(r.id) AS review_count,

        COALESCE(MAX(pr.discount_percent), 0) AS discount_percent,
        CASE
            WHEN MAX(pr.discount_percent) IS NOT NULL
            THEN ROUND(p.original_price * (100 - MAX(pr.discount_percent)) / 100, 2)
            ELSE NULL
        END AS sale_price,

        c.category_name

    FROM products p
    LEFT JOIN categories c ON p.category_id = c.id
    LEFT JOIN reviews r ON p.id = r.product_id
    LEFT JOIN promotions pr
        ON p.id = pr.product_id
       AND (pr.start_date IS NULL OR pr.start_date <= NOW())
       AND (pr.end_date IS NULL OR pr.end_date >= NOW())
"""
            ;
    private static final String BASE_SELECT = """
        SELECT
            p.id,
            p.category_id,
            p.name,
            p.description,
            p.original_price,
            p.image_url,
            p.stock_quantity,
            p.total_sold,
            COALESCE(ROUND(AVG(r.rating), 1), 0) AS avg_rating,
            COUNT(r.id) AS review_count,
        
            COALESCE(MAX(pr.discount_percent), 0) AS discount_percent,
            CASE
                WHEN MAX(pr.discount_percent) IS NOT NULL
                THEN ROUND(p.original_price * (100 - MAX(pr.discount_percent)) / 100, 2)
                ELSE NULL
            END AS sale_price
        
        FROM products p
        LEFT JOIN reviews r ON p.id = r.product_id
        LEFT JOIN promotions pr
            ON p.id = pr.product_id
           AND (pr.start_date IS NULL OR pr.start_date <= NOW())
           AND (pr.end_date IS NULL OR pr.end_date >= NOW())
        
        GROUP BY
            p.id,
            p.category_id,
            p.name,
            p.description,
            p.original_price,
            p.image_url,
            p.stock_quantity,
            p.total_sold
        
    """;

    public List<Product> getBestSellingProducts(int limit) {
        String sql = BASE_SELECT + " ORDER BY p.total_sold DESC LIMIT ?";
        return getProductsByLimit(sql, limit);
    }

    public List<Product> getNewestProducts(int limit) {
        String sql = BASE_SELECT + " ORDER BY p.id DESC LIMIT ?";
        return getProductsByLimit(sql, limit);
    }

    public List<Product> getTopRatedProducts(int limit) {
        String sql = BASE_SELECT + " ORDER BY avg_rating DESC, review_count DESC LIMIT ?";
        return getProductsByLimit(sql, limit);
    }

    public List<Product> getTopSellingByCategory(int categoryId, int limit) {
        String sql = """
            SELECT * FROM (
                """ + BASE_SELECT + """
            ) t
            WHERE t.category_id = ?
            ORDER BY t.total_sold DESC
            LIMIT ?
        """;

        List<Product> list = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> getProductsByCategoryWithFilter(
            int categoryId,
            Integer minPrice,
            Integer maxPrice,
            Integer rating,
            ProductSort sort,
            int offset,
            int limit
    ) {

        List<Product> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT * FROM (
                """ + BASE_SELECT + """
            ) t
            WHERE t.category_id = ?
        """);

        if (minPrice != null) sql.append(" AND t.original_price >= ?");
        if (maxPrice != null) sql.append(" AND t.original_price <= ?");
        if (rating != null)   sql.append(" AND t.avg_rating >= ?");

        if (sort != null) {
            switch (sort) {
                case PRICE_ASC  -> sql.append(" ORDER BY t.original_price ASC");
                case PRICE_DESC -> sql.append(" ORDER BY t.original_price DESC");
                case NEWEST     -> sql.append(" ORDER BY t.id DESC");
                default         -> sql.append(" ORDER BY t.total_sold DESC");
            }
        } else {
            sql.append(" ORDER BY t.total_sold DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, categoryId);
            if (minPrice != null) ps.setInt(idx++, minPrice);
            if (maxPrice != null) ps.setInt(idx++, maxPrice);
            if (rating != null)   ps.setInt(idx++, rating);
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countProductsByCategoryWithFilter(
            int categoryId,
            Integer minPrice,
            Integer maxPrice,
            Integer rating
    ) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM (
                SELECT p.id
                FROM products p
                LEFT JOIN reviews r ON p.id = r.product_id
                WHERE p.category_id = ?
        """);

        if (minPrice != null) sql.append(" AND p.original_price >= ?");
        if (maxPrice != null) sql.append(" AND p.original_price <= ?");

        sql.append(" GROUP BY p.id ");

        if (rating != null) sql.append(" HAVING AVG(r.rating) >= ? ");

        sql.append(") t");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, categoryId);
            if (minPrice != null) ps.setInt(idx++, minPrice);
            if (maxPrice != null) ps.setInt(idx++, maxPrice);
            if (rating != null)   ps.setInt(idx, rating);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Product getProductById(int id) {
        String sql = """
            SELECT * FROM (
                """ + BASE_SELECT + """
            ) t
            WHERE t.id = ?
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapProduct(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> getRelatedProducts(int categoryId, int excludeId, int limit) {

        String sql = """
        SELECT * FROM (
            """ + BASE_SELECT + """
        ) t
        WHERE t.category_id = ?
          AND t.id <> ?
        ORDER BY t.total_sold DESC
        LIMIT ?
    """;

        List<Product> list = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, excludeId);
            ps.setInt(3, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private List<Product> getProductsByLimit(String sql, int limit) {
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> searchProductsByName(String keyword, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = BASE_SELECT + """
            WHERE LOWER(name) LIKE LOWER(?)
            ORDER BY total_sold DESC, avg_rating DESC
            LIMIT ?
        """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setInt(2, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                Category category = new Category();
                category.setId(rs.getInt("category_id"));
                p.setCategory(category);
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setOriginalPrice(rs.getDouble("original_price"));
                p.setDiscountPercent(rs.getInt("discount_percent"));
                p.setSalePrice(rs.getDouble("sale_price"));
                p.setImage(rs.getString("image_url"));
                p.setStockQuantity(rs.getInt("stock_quantity"));
                p.setTotalSold(rs.getInt("total_sold"));
                p.setAvgRating(rs.getDouble("avg_rating"));
                p.setReviewCount(rs.getInt("review_count"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Product mapProduct(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        Integer categoryId = (Integer) rs.getObject("category_id");
        if (categoryId != null) {
            Category category = new Category();
            category.setId(categoryId);
            p.setCategory(category);
        }
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setOriginalPrice(rs.getDouble("original_price"));
        p.setImage(rs.getString("image_url"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setTotalSold(rs.getInt("total_sold"));
        p.setAvgRating(rs.getDouble("avg_rating"));
        p.setReviewCount(rs.getInt("review_count"));

        // discount
        int discount = rs.getInt("discount_percent");
        if (discount > 0) {
            p.setDiscountPercent(discount);
            p.setSalePrice(rs.getDouble("sale_price"));
        }

        return p;
    }

    public int getTotalProducts() {
        String sql = "SELECT COUNT(*) as total FROM products";
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

    public List<Product> getTopSellingProducts(int limit) {
        String sql = BASE_SELECT + " ORDER BY total_sold DESC LIMIT ?";
        return getProductsByLimit(sql, limit);
    }

    public List<Product> getAllProducts() {
        String sql = BASE_SELECT + " ORDER BY id DESC";
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getProductsWithPagination(int offset, int limit) {
        String sql = BASE_SELECT + " ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertProduct(Product product,Category category) {
        String sql = """
        INSERT INTO products (
            category_id, name, description, original_price,
            image_url, stock_quantity, total_sold, avg_rating, review_count
        )
        VALUES (?, ?, ?, ?, ?, ?, 0, 0, 0)
    """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, product.getCategory() != null ? product.getCategory().getId() : null);
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getOriginalPrice());
            ps.setString(5, product.getImage());
            ps.setInt(6, product.getStockQuantity());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProduct(Product product) {
        String sql = """
        UPDATE products
        SET category_id = ?, name = ?, description = ?,
            original_price = ?, image_url = ?, stock_quantity = ?
        WHERE id = ?
    """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, product.getCategory() != null ? product.getCategory().getId() : null);
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getOriginalPrice());
            ps.setString(5, product.getImage());
            ps.setInt(6, product.getStockQuantity());
            ps.setInt(7, product.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Product> searchProducts(String keyword) {

        List<Product> list = new ArrayList<>();

        String sql = SEARCH_SELECT + """
        WHERE LOWER(p.name) LIKE LOWER(?)
           OR LOWER(p.description) LIKE LOWER(?)
           OR LOWER(c.category_name) LIKE LOWER(?)
        GROUP BY
            p.id,
            p.category_id,
            p.name,
            p.description,
            p.original_price,
            p.image_url,
            p.stock_quantity,
            p.total_sold,
            c.category_name
        ORDER BY p.total_sold DESC, avg_rating DESC
    """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}

