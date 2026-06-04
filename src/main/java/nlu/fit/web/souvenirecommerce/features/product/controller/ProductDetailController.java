package nlu.fit.web.souvenirecommerce.features.product.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.product.dto.ProductDetailDTO;
import nlu.fit.web.souvenirecommerce.features.product.service.ProductService;

import java.io.IOException;

@WebServlet("/product")
public class ProductDetailController extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        productService = new ProductService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

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

        request.setAttribute("headerMode", "BREADCRUMB");
        request.setAttribute("breadcrumbCategory", dto.getCategory());
        request.setAttribute("breadcrumbProduct", dto.getProduct());

        request.setAttribute("pageTitle", dto.getProduct().getName());
        request.setAttribute("contentPage", "/WEB-INF/views/product/detail.jsp");
        request.setAttribute("pageCss", "ProductDetail.css");
        request.setAttribute("pageJs", "ProductDetail.js");

        request.getRequestDispatcher("/WEB-INF/layout/base.jsp")
                .forward(request, response);
    }
}