package nlu.fit.web.souvenirecommerce.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.UserDAO;
import nlu.fit.web.souvenirecommerce.model.User;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/register")
public class SignupAPIServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        PrintWriter out = resp.getWriter();
        JsonObject jsonResponse = new JsonObject();

        // Validate input
        if (fullName == null || fullName.trim().isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Họ tên không được để trống");
            out.print(jsonResponse.toString());
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Email không được để trống");
            out.print(jsonResponse.toString());
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Email không hợp lệ");
            out.print(jsonResponse.toString());
            return;
        }

        if (phone == null || phone.trim().isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Số điện thoại không được để trống");
            out.print(jsonResponse.toString());
            return;
        }

        if (!phone.matches("[0-9]{10,20}")) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Số điện thoại phải từ 10-20 chữ số");
            out.print(jsonResponse.toString());
            return;
        }

        if (password == null || password.isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Mật khẩu không được để trống");
            out.print(jsonResponse.toString());
            return;
        }

        if (password.length() < 8) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Mật khẩu phải ít nhất 8 ký tự");
            out.print(jsonResponse.toString());
            return;
        }

        if (!password.equals(confirmPassword)) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Mật khẩu xác nhận không trùng khớp");
            out.print(jsonResponse.toString());
            return;
        }

        // Check if email already exists
        if (userDAO.emailExists(email)) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Email này đã được đăng ký");
            out.print(jsonResponse.toString());
            return;
        }

        // Register user
        if (userDAO.register(email, password, fullName, phone)) {
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Đăng ký thành công! Vui lòng đăng nhập");
            out.print(jsonResponse.toString());
        } else {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại");
            out.print(jsonResponse.toString());
        }
    }
}
