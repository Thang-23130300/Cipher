package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.dao.UserDAO;
import nlu.fit.web.souvenirecommerce.model.User;

import java.io.IOException;

@WebServlet(name = "LoginController", value = "/login")
public class LoginController extends HttpServlet {
    private UserDAO dao = new UserDAO(); // Khai báo dùng chung cho các method
    private AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển hướng về trang login khi vào link /login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Lấy dữ liệu từ JSP - Phải khớp với name="loginDetail"
        String loginDetail = request.getParameter("loginDetail");
        String password = request.getParameter("password");

        // Kiểm tra null để tránh lỗi 500 NullPointerException
        if (loginDetail == null || loginDetail.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập Email hoặc Số điện thoại!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // 2. Gọi DAO để kiểm tra đăng nhập
        User authUser = dao.login(loginDetail, password);

        if (authUser != null) {
            // ĐĂNG NHẬP THÀNH CÔNG: Lưu user vào Session
            HttpSession session = request.getSession();
            session.setAttribute("userInSession", authUser);
            session.setAttribute("user", authUser);

            boolean canAccessAdmin = "Admin".equalsIgnoreCase(authUser.getRole())
                    || authorizationDAO.hasPermission(authUser.getId(), "dashboard", "read");

            // Nếu có quyền quản trị thì chuyển đến trang admin dashboard
            if (canAccessAdmin) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                // User thường thì chuyển về trang chủ
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
            // ĐĂNG NHẬP THẤT BẠI: Hiện lỗi
            request.setAttribute("error", "Tài khoản hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
