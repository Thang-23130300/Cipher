package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.ProductSpecification;
import nlu.fit.web.souvenirecommerce.util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationDAO {

    private static final String SELECT_BY_PRODUCT_ID = """
        SELECT id, product_id, spec_name, spec_value
        FROM product_specifications
        WHERE product_id = ?
    """;

    public List<ProductSpecification> getByProductId(Long productId) {
        List<ProductSpecification> list = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_PRODUCT_ID)) {

            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapSpecification(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private ProductSpecification mapSpecification(ResultSet rs) throws Exception {
        return new ProductSpecification(
                rs.getInt("id"),
                rs.getLong("product_id"),
                rs.getString("spec_name"),
                rs.getString("spec_value")
        );
    }
}
