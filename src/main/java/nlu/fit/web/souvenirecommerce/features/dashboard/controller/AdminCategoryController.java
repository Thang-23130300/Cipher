package nlu.fit.web.souvenirecommerce.features.dashboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.legacy.dao.CategoryDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.io.IOException;

@WebServlet("/admin/categories")
public class AdminCategoryController extends HttpServlet {

    private CategoryDAO categoryDAO;

    @Override
    public void init() {
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", categoryDAO.getAllCategories());
        req.getRequestDispatcher("/admin/categories.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                Category category = new Category();
                category.setCategoryName(req.getParameter("name"));
                category.setImage(req.getParameter("imageUrl"));

                if (categoryDAO.insertCategory(category)) {
                    req.setAttribute("message", "Thêm danh mục thành công!");
                    req.setAttribute("messageType", "success");
                } else {
                    req.setAttribute("message", "Thêm danh mục thất bại!");
                    req.setAttribute("messageType", "error");
                }

            } else if ("edit".equals(action)) {
                Category category = new Category();
                category.setId(Long.parseLong(req.getParameter("id")));
                category.setCategoryName(req.getParameter("name"));
                category.setImage(req.getParameter("imageUrl"));

                if (categoryDAO.updateCategory(category)) {
                    req.setAttribute("message", "Cập nhật danh mục thành công!");
                    req.setAttribute("messageType", "success");
                } else {
                    req.setAttribute("message", "Cập nhật danh mục thất bại!");
                    req.setAttribute("messageType", "error");
                }

            } else if ("delete".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                int productCount = categoryDAO.getProductCountByCategory(id);

                if (productCount > 0) {
                    req.setAttribute("message", "Không thể xóa danh mục có " + productCount + " sản phẩm!");
                    req.setAttribute("messageType", "error");
                } else if (categoryDAO.deleteCategory(id)) {
                    req.setAttribute("message", "Xóa danh mục thành công!");
                    req.setAttribute("messageType", "success");
                } else {
                    req.setAttribute("message", "Xóa danh mục thất bại!");
                    req.setAttribute("messageType", "error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            req.setAttribute("messageType", "error");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }
}
