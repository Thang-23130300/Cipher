package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LogoutController", value = "/logout")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Lấy session hiện tại
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 2. Hủy bỏ session (xóa userInSession)
            session.invalidate();
        }
        // 3. Chuyển về trang đăng nhập
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
