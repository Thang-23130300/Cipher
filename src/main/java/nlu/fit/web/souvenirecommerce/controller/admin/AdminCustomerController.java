package nlu.fit.web.souvenirecommerce.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl;

import java.io.IOException;

@WebServlet("/admin/customers")
public class AdminCustomerController extends HttpServlet {

    private UserDAOImpl userDAOImpl;

    @Override
    public void init() {

            userDAOImpl = new UserDAOImpl();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        int totalCustomers = userDAOImpl.getTotalCustomers();
        int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);

        req.setAttribute("customers", userDAOImpl.getCustomersWithPagination(offset, pageSize));
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalCustomers", totalCustomers);

        String message = (String) req.getSession().getAttribute("message");
        String messageType = (String) req.getSession().getAttribute("messageType");
        if (message != null) {
            req.setAttribute("message", message);
            req.setAttribute("messageType", messageType);
            req.getSession().removeAttribute("message");
            req.getSession().removeAttribute("messageType");
        }

        req.getRequestDispatcher("/admin/customers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                String fullName = req.getParameter("fullName");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                String phone = req.getParameter("phone");

                if (userDAOImpl.insertUser(fullName, email, password, phone)) {
                    req.getSession().setAttribute("message", "Thêm khách hàng thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Thêm khách hàng thất bại!");
                    req.getSession().setAttribute("messageType", "error");
                }

            } else if ("edit".equals(action)) {
                int userId = Integer.parseInt(req.getParameter("id"));
                String fullName = req.getParameter("fullName");
                String email = req.getParameter("email");
                String phone = req.getParameter("phone");

                if (userDAOImpl.updateUser(userId, fullName, email, phone)) {
                    req.getSession().setAttribute("message", "Cập nhật khách hàng thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Cập nhật khách hàng thất bại!");
                    req.getSession().setAttribute("messageType", "error");
                }

            } else if ("toggleStatus".equals(action)) {
                int userId = Integer.parseInt(req.getParameter("id"));
                String currentStatus = req.getParameter("currentStatus");
                String newStatus = "Active".equals(currentStatus) ? "Banned" : "Active";

                if (userDAOImpl.updateUserStatus(userId, newStatus)) {
                    req.getSession().setAttribute("message", "Cập nhật trạng thái thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Cập nhật trạng thái thất bại!");
                    req.getSession().setAttribute("messageType", "error");
                }

            } else if ("delete".equals(action)) {
                int userId = Integer.parseInt(req.getParameter("id"));

                if (userDAOImpl.deleteUser(userId)) {
                    req.getSession().setAttribute("message", "Xóa khách hàng thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Xóa khách hàng thất bại!");
                    req.getSession().setAttribute("messageType", "error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            req.getSession().setAttribute("messageType", "error");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/customers");
    }
}
