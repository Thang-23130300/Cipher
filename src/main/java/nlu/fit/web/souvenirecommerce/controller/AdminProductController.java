package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.CategoryDAO;
import nlu.fit.web.souvenirecommerce.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.io.IOException;

@WebServlet("/admin/products")
public class AdminProductController extends HttpServlet {

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String searchQuery = req.getParameter("search");
        int page = 1;
        int pageSize = 20;

        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * pageSize;
        int totalProducts;

        // If search query exists, search products
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            var searchResults = productDAO.searchProducts(searchQuery.trim());
            totalProducts = searchResults.size();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            // Paginate search results
            int fromIndex = Math.min(offset, searchResults.size());
            int toIndex = Math.min(offset + pageSize, searchResults.size());
            var paginatedResults = searchResults.subList(fromIndex, toIndex);

            req.setAttribute("products", paginatedResults);
            req.setAttribute("searchQuery", searchQuery);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalProducts", totalProducts);
        } else {
            // Normal pagination
            totalProducts = productDAO.getTotalProducts();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            req.setAttribute("products", productDAO.getProductsWithPagination(offset, pageSize));
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalProducts", totalProducts);
        }

        req.setAttribute("categories", categoryDAO.getAllCategories());
        req.getRequestDispatcher("/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                Product product = new Product();
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                Category category = new Category();
                category.setId(categoryId);
                product.setCategory(category);
                product.setName(req.getParameter("name"));
                product.setDescription(req.getParameter("description"));
                product.setOriginalPrice(Double.parseDouble(req.getParameter("price")));
                product.setImage(req.getParameter("imageUrl"));
                product.setStockQuantity(Integer.parseInt(req.getParameter("stock")));

                // Handle discount
                String discountParam = req.getParameter("discountPercent");
                String salePriceParam = req.getParameter("salePrice");
                if (discountParam != null && !discountParam.isEmpty()) {
                    int discount = Integer.parseInt(discountParam);
                    product.setDiscountPercent(discount);
                    if (discount > 0 && salePriceParam != null && !salePriceParam.isEmpty()) {
                        product.setSalePrice(Double.parseDouble(salePriceParam));
                    }
                }

                if (productDAO.insertProduct(product)) {
                    req.setAttribute("message", "Thêm sản phẩm thành công!");
                    req.setAttribute("messageType", "success");
                } else {
                    req.setAttribute("message", "Thêm sản phẩm thất bại!");
                    req.setAttribute("messageType", "error");
                }

            } else if ("edit".equals(action)) {
                Product product = new Product();
                product.setId(Integer.parseInt(req.getParameter("id")));
                Category category = new Category();
                category.setId(Integer.parseInt(req.getParameter("categoryId")));
                product.setCategory(category);
                product.setName(req.getParameter("name"));
                product.setDescription(req.getParameter("description"));
                product.setOriginalPrice(Double.parseDouble(req.getParameter("price")));
                product.setImage(req.getParameter("imageUrl"));
                product.setStockQuantity(Integer.parseInt(req.getParameter("stock")));

                // Handle discount
                String discountParam = req.getParameter("discountPercent");
                String salePriceParam = req.getParameter("salePrice");
                if (discountParam != null && !discountParam.isEmpty()) {
                    int discount = Integer.parseInt(discountParam);
                    product.setDiscountPercent(discount);
                    if (discount > 0 && salePriceParam != null && !salePriceParam.isEmpty()) {
                        product.setSalePrice(Double.parseDouble(salePriceParam));
                    }
                }

                if (productDAO.updateProduct(product)) {
                    req.setAttribute("message", "Cập nhật sản phẩm thành công!");
                    req.setAttribute("messageType", "success");
                } else {
                    req.setAttribute("message", "Cập nhật sản phẩm thất bại!");
                    req.setAttribute("messageType", "error");
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));

                if (productDAO.deleteProduct(id)) {
                    req.setAttribute("message", "Xóa sản phẩm thành công!");
                    req.setAttribute("messageType", "success");
                } else {
                    req.setAttribute("message", "Xóa sản phẩm thất bại!");
                    req.setAttribute("messageType", "error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            req.setAttribute("messageType", "error");
        }

        // Redirect to avoid form resubmission
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}