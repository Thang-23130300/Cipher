package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.Enums.ProductSort;
import nlu.fit.web.souvenirecommerce.dto.ProductTypeDTO;
import nlu.fit.web.souvenirecommerce.service.ProductTypeService;

import java.io.IOException;

@WebServlet("/category")
public class    ProductTypeController extends HttpServlet {

    private ProductTypeService productTypeService;

    @Override
    public void init() {
        productTypeService = new ProductTypeService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* ===== 1. VALIDATE CATEGORY ID ===== */
        Long categoryId;
        try {
            categoryId = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        /* ===== 2. FILTER PARAMS ===== */
        Integer minPrice = parseInteger(request.getParameter("minPrice"));
        Integer maxPrice = parseInteger(request.getParameter("maxPrice"));
        ProductSort sort = parseSort(request.getParameter("sort"));

        int page = parseInteger(request.getParameter("page"), 1);
        Integer rating = parseInteger(request.getParameter("rating"));

        /* ===== 3. SERVICE ===== */
        ProductTypeDTO dto = productTypeService.getProductType(
                categoryId,
                minPrice,
                maxPrice,
                rating,
                sort,
                page
        );

        if (dto == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        /* ===== 4. HEADER ===== */
        request.setAttribute("headerMode", "BREADCRUMB");
        request.setAttribute("breadcrumbCategory", dto.getCategory());
        request.setAttribute("enableHeaderOverlay", true);

        /* ===== 5. PAGE DATA ===== */
        request.setAttribute("data", dto);

        /* ===== 6. LAYOUT CONFIG ===== */
        request.setAttribute("pageTitle", dto.getCategory().getCategoryName());
        request.setAttribute("contentPage", "productType.jsp");
        request.setAttribute("pageCss", "PTypeMain.css");
        request.setAttribute("pageJs", "ProductType.js");

        /* ===== 7. FORWARD QUA LAYOUT ===== */
        request.getRequestDispatcher("layoutMain.jsp")
                .forward(request, response);

    }

    /* ================= UTIL ================= */

    private Integer parseInteger(String value) {
        try {
            return value != null && !value.isEmpty()
                    ? Integer.parseInt(value)
                    : null;
        } catch (Exception e) {
            return null;
        }
    }

    private int parseInteger(String value, int defaultValue) {
        try {
            return value != null
                    ? Integer.parseInt(value)
                    : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private ProductSort parseSort(String sort) {
        if (sort == null) return ProductSort.POPULAR;

        return switch (sort) {
            case "price_asc" -> ProductSort.PRICE_ASC;
            case "price_desc" -> ProductSort.PRICE_DESC;
            case "newest" -> ProductSort.NEWEST;
            default -> ProductSort.POPULAR;
        };
    }
}
