package nlu.fit.web.souvenirecommerce.features.auth.controller;

import com.google.gson.JsonObject;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import nlu.fit.web.souvenirecommerce.features.auth.dao.SignupVerificationCodeDAO;
import nlu.fit.web.souvenirecommerce.legacy.dao.IUserDAO;
import nlu.fit.web.souvenirecommerce.features.auth.dao.AuthDAO;
import nlu.fit.web.souvenirecommerce.features.auth.service.impl.EmailServiceImpl;
import nlu.fit.web.souvenirecommerce.common.utils.EmailUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@WebServlet("/api/signup/send-code")
@Slf4j
public class SendSignupCodeServlet extends HttpServlet {
    private static final SecureRandom RANDOM = new SecureRandom();

    private IUserDAO userDAO;
    private EmailServiceImpl emailService;
    private SignupVerificationCodeDAO signupVerificationCodeDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new AuthDAO();
        emailService = new EmailServiceImpl();
        signupVerificationCodeDAO = new SignupVerificationCodeDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        String email = EmailUtil.normalizeEmail(req.getParameter("email"));
        JsonObject jsonResponse = new JsonObject();

        if (email == null || email.isEmpty()) {
            log.warn("Gửi mã đăng ký thất bại: email rỗng");
            writeJson(resp, jsonResponse, "error", "Email không được để trống");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.warn("Gửi mã đăng ký thất bại: email không hợp lệ {}", email);
            writeJson(resp, jsonResponse, "error", "Email không hợp lệ");
            return;
        }

        if (userDAO.hasEmailExist(email)) {
            log.warn("Gửi mã đăng ký thất bại: email đã tồn tại {}", email);
            writeJson(resp, jsonResponse, "error", "Email này đã được đăng ký");
            return;
        }

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        try {
            signupVerificationCodeDAO.createCode(email, code, expiresAt);
            emailService.sendSignupVerificationCode(email, code);
        } catch (MessagingException | RuntimeException e) {
            log.error("Gửi mã đăng ký thất bại: email={}", email, e);
            writeJson(resp, jsonResponse, "error", "Không gửi được mã xác thực. Vui lòng kiểm tra cấu hình email");
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("signupEmail", email);
        session.removeAttribute("signupVerifiedEmail");

        log.info("Gửi mã đăng ký thành công: email={}, expiresAt={}", email, expiresAt);
        writeJson(resp, jsonResponse, "success", "Mã xác thực đã được gửi tới email của bạn");
    }

    private void writeJson(HttpServletResponse resp, JsonObject jsonResponse, String status, String message) throws IOException {
        jsonResponse.addProperty("status", status);
        jsonResponse.addProperty("message", message);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse.toString());
    }
}
