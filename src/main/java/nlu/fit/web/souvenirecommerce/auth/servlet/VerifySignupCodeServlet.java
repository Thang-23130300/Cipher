package nlu.fit.web.souvenirecommerce.auth.servlet;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.IUserDAO;
import nlu.fit.web.souvenirecommerce.auth.dao.AuthDAO;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/signup/verify-code")
public class VerifySignupCodeServlet extends HttpServlet {
    private final IUserDAO userDAO = new AuthDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        String email = normalizeEmail(req.getParameter("email"));
        String code = req.getParameter("code") == null ? "" : req.getParameter("code").trim();
        JsonObject jsonResponse = new JsonObject();

        if (email == null || email.isEmpty()) {
            writeJson(resp, jsonResponse, "error", "Email không được để trống");
            return;
        }

        if (!code.matches("^[0-9]{6}$")) {
            writeJson(resp, jsonResponse, "error", "Mã xác thực gồm 6 chữ số");
            return;
        }

        if (userDAO.hasEmailExist(email)) {
            writeJson(resp, jsonResponse, "error", "Email này đã được đăng ký");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            writeJson(resp, jsonResponse, "error", "Vui lòng gửi mã xác thực trước");
            return;
        }

        String sessionEmail = (String) session.getAttribute("signupEmail");
        String sessionOtp = (String) session.getAttribute("signupOtp");
        Long expiresAt = (Long) session.getAttribute("signupOtpExpiresAt");

        if (!email.equals(sessionEmail) || sessionOtp == null || expiresAt == null) {
            writeJson(resp, jsonResponse, "error", "Vui lòng gửi mã xác thực trước");
            return;
        }

        if (System.currentTimeMillis() > expiresAt) {
            clearOtp(session);
            writeJson(resp, jsonResponse, "error", "Mã xác thực đã hết hạn. Vui lòng gửi lại mã");
            return;
        }

        if (!sessionOtp.equals(code)) {
            writeJson(resp, jsonResponse, "error", "Mã xác thực không đúng");
            return;
        }

        session.setAttribute("signupVerifiedEmail", email);
        clearOtp(session);
        writeJson(resp, jsonResponse, "success", "Email đã được xác thực");
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void clearOtp(HttpSession session) {
        session.removeAttribute("signupOtp");
        session.removeAttribute("signupOtpExpiresAt");
    }

    private void writeJson(HttpServletResponse resp, JsonObject jsonResponse, String status, String message) throws IOException {
        jsonResponse.addProperty("status", status);
        jsonResponse.addProperty("message", message);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse.toString());
    }
}
