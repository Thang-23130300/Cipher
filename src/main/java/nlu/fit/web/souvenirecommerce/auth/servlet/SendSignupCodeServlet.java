package nlu.fit.web.souvenirecommerce.auth.servlet;

import com.google.gson.JsonObject;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.IUserDAO;
import nlu.fit.web.souvenirecommerce.auth.dao.AuthDAO;
import nlu.fit.web.souvenirecommerce.service.impl.EmailServiceImpl;
import nlu.fit.web.souvenirecommerce.util.EmailUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;

@WebServlet("/api/signup/send-code")
public class SendSignupCodeServlet extends HttpServlet {
    private static final long OTP_TTL_MILLIS = 10 * 60 * 1000L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private IUserDAO userDAO;
    private EmailServiceImpl emailService;

    @Override
    public void init() throws ServletException {
        userDAO = new AuthDAO();
        emailService = new EmailServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        String email = EmailUtil.normalizeEmail(req.getParameter("email"));
        JsonObject jsonResponse = new JsonObject();

        if (email == null || email.isEmpty()) {
            writeJson(resp, jsonResponse, "error", "Email không được để trống");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            writeJson(resp, jsonResponse, "error", "Email không hợp lệ");
            return;
        }

        if (userDAO.hasEmailExist(email)) {
            writeJson(resp, jsonResponse, "error", "Email này đã được đăng ký");
            return;
        }

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        try {
            emailService.sendSignupVerificationCode(email, code);
        } catch (MessagingException e) {
            e.printStackTrace();
            writeJson(resp, jsonResponse, "error", "Không gửi được mã xác thực. Vui lòng kiểm tra cấu hình email");
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("signupEmail", email);
        session.setAttribute("signupOtp", code);
        session.setAttribute("signupOtpExpiresAt", System.currentTimeMillis() + OTP_TTL_MILLIS);
        session.removeAttribute("signupVerifiedEmail");

        writeJson(resp, jsonResponse, "success", "Mã xác thực đã được gửi tới email của bạn");
    }

    private void writeJson(HttpServletResponse resp, JsonObject jsonResponse, String status, String message) throws IOException {
        jsonResponse.addProperty("status", status);
        jsonResponse.addProperty("message", message);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse.toString());
    }
}
