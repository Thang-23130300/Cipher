package nlu.fit.web.souvenirecommerce.features.product.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.product.dto.ProductDetailDTO;
import nlu.fit.web.souvenirecommerce.features.product.service.ProductService;
import nlu.fit.web.souvenirecommerce.features.product.service.ReviewService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

@WebServlet("/product")
public class ProductDetailController extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() {
        productService = new ProductService();
        reviewService = new ReviewService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Long productId;

        try {
            productId = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        ProductDetailDTO dto = productService.getProductDetail(productId);

        if (dto == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("data", dto);

        Integer currentUserId = getCurrentUserId(request);

        boolean isLoggedIn = currentUserId != null;
        boolean canReview = false;

        if (isLoggedIn) {
            canReview = reviewService.canReview(currentUserId, productId);
        }

        request.setAttribute("isLoggedIn", isLoggedIn);
        request.setAttribute("canReview", canReview);

        request.setAttribute("headerMode", "BREADCRUMB");
        request.setAttribute("breadcrumbCategory", dto.getCategory());
        request.setAttribute("breadcrumbProduct", dto.getProduct());

        request.setAttribute("pageTitle", dto.getProduct().getName());
        request.setAttribute("contentPage", "/WEB-INF/views/product/detail.jsp");
        request.setAttribute("pageCss", "ProductDetail.css");
        request.setAttribute("pageJs", "ProductDetail.js");

        request.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(request, response);
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        String[] commonKeys = {"user", "currentUser", "authUser", "userInSession", "loggedInUser", "account"};

        for (String key : commonKeys) {
            Integer userId = extractUserId(session.getAttribute(key));

            if (userId != null) {
                return userId;
            }
        }

        Enumeration<String> names = session.getAttributeNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Integer userId = extractUserId(session.getAttribute(name));

            if (userId != null) {
                return userId;
            }
        }

        return null;
    }

    private Integer extractUserId(Object userObj) {
        if (userObj == null) {
            return null;
        }

        try {
            Method getIdMethod = userObj.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(userObj);

            if (id instanceof Number) {
                return ((Number) id).intValue();
            }

            if (id instanceof String) {
                return Integer.parseInt((String) id);
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}