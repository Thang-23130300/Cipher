package nlu.fit.web.souvenirecommerce.account.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = "/user/account/profile")
public class ProfileServlet extends HttpServlet {


    /**
     * Hiển thị trang hồ sơ người dùng
     * - Kiểm tra nếu người dùng chưa đăng nhập thì chuyển hướng về trang đăng nhập
     * - Nếu đã đăng nhập, thiết lập các thuộc tính cần thiết cho trang hồ sơ và chuyển tiếp đến layout chính để hiển thị
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("pageTitle", "Hồ sơ");
        req.setAttribute("pageCss", "user/profile.css");
        req.setAttribute("pageJs", "user/profile.js");
        req.setAttribute("contentPage", "/WEB-INF/views/user/account_layout.jsp");

        req.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(req, resp);
    }
}
