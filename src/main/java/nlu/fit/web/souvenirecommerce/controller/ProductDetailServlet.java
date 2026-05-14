package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.dao.ProductSpecificationDAO;
import nlu.fit.web.souvenirecommerce.dao.PromotionDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.ProductSpecification;
import nlu.fit.web.souvenirecommerce.model.Promotion;

import java.io.IOException;
import java.util.List;

@WebServlet("/productDetail")
public class ProductDetailServlet extends HttpServlet {

    private ProductDAO productDAO;
    private PromotionDAO promotionDAO;
    private ProductSpecificationDAO specDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
        promotionDAO = new PromotionDAO();
        specDAO = new ProductSpecificationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* ================= 1. Lấy productId ================= */
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("home");
            return;
        }

        Long productId;
        try {
            productId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("home");
            return;
        }

        /* ================= 2. Lấy Product ================= */
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            response.sendRedirect("home");
            return;
        }

        /* ================= 3. Promotion ================= */
        Promotion promotion = promotionDAO.getActivePromotionByProductId(productId);
        /* ================= 4. Specification ================= */
        List<ProductSpecification> specs = specDAO.getByProductId(productId);

        request.setAttribute("specs", specs);

        /* ================= 5. Related Products ================= */
        List<Product> relatedProducts = null;
        if (product.getCategoryId() != null) {
            relatedProducts = productDAO.getRelatedProducts(
                    product.getCategoryId(), productId, 5);
        }

        /* ================= 6. Gửi dữ liệu sang JSP ================= */
        request.setAttribute("product", product);
        request.setAttribute("promotion", promotion);
        request.setAttribute("specs", specs);
        request.setAttribute("relatedProducts", relatedProducts);
        request.setAttribute("productId", productId); // ⭐ rất quan trọng cho ReviewServlet

        /* ================= 7. Forward ================= */
        request.getRequestDispatcher("/WEB-INF/views/productDetail.jsp")
                .forward(request, response);
    }
    /* ================= HELPER ================= */

    private Long parseProductId(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            return Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect("home");
            return -1L;
        }
    }
}
