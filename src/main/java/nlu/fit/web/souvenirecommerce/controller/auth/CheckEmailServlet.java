package nlu.fit.web.souvenirecommerce.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.UserDAO;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/check-email")
public class CheckEmailServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        PrintWriter out = resp.getWriter();
        JsonObject jsonResponse = new JsonObject();

        if (email == null || email.trim().isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Email không được để trống");
            out.print(jsonResponse.toString());
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Email không hợp lệ");
            out.print(jsonResponse.toString());
            return;
        }

        // Check if email exists
        boolean exists = userDAO.emailExists(email);

        if (exists) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Email này đã được đăng ký");
        } else {
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Email này có thể sử dụng");
            jsonResponse.addProperty("email", email);
        }

        out.print(jsonResponse.toString());
    }
}
