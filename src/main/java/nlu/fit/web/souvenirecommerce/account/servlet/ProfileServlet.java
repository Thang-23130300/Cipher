package nlu.fit.web.souvenirecommerce.account.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import nlu.fit.web.souvenirecommerce.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
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
        req.setAttribute("pageCss", "account/account-layout.css");
        req.setAttribute("contentCss", "account/profile.css");
        req.setAttribute("pageJs", "account/profile.js");
        req.setAttribute("contentPage", "/WEB-INF/views/account/account_layout.jsp");

        req.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


    }

}
