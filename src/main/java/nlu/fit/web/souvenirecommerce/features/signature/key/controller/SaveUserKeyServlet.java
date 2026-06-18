package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.KeyOtpService;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.PublicKeyFingerprintService;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/signature/keys/save")
public class SaveUserKeyServlet extends HttpServlet {
    private final UserKeyService userKeyService = new UserKeyService();
    private final KeyOtpService keyOtpService = new KeyOtpService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String publicKey = request.getParameter("publicKey");
        String returnUrl = sanitizeReturnUrl(request.getParameter("returnUrl"));

        try {
            // Kiểm tra sơ bộ định dạng PEM để phản hồi nhanh
            if (publicKey == null || publicKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Public key không được để trống");
            }
            String normalized = publicKey.trim();
            if (!normalized.contains("-----BEGIN PUBLIC KEY-----")
                    || !normalized.contains("-----END PUBLIC KEY-----")) {
                throw new IllegalArgumentException("Public key phải đúng định dạng PEM");
            }

            // Kiểm tra xem có trùng với key đang hoạt động không
            userKeyService.getActiveKey(currentUser.getId()).ifPresent(activeKey -> {
                if (activeKey.getPublicKey().replaceAll("\\s", "").equals(normalized.replaceAll("\\s", ""))) {
                    throw new IllegalArgumentException("Public key này đang được sử dụng");
                }
            });

            // Gửi OTP và lưu khóa chờ xác thực vào database
            keyOtpService.generateAndSendOtp(currentUser.getId(), currentUser.getEmail(), publicKey);

            // Ghi nhận trạng thái đang chờ OTP và lưu returnUrl vào session
            request.getSession().setAttribute("keyChangePending", true);
            if (returnUrl != null) {
                request.getSession().setAttribute("keyChangeReturnUrl", returnUrl);
            }
            request.getSession().setAttribute("success", "Mã xác thực OTP đã được gửi đến email đăng ký của bạn.");
        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
        }

        String redirectUrl = request.getContextPath() + "/key-management";
        if (returnUrl != null) {
            redirectUrl += "?returnUrl=" + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8);
        }
        response.sendRedirect(redirectUrl);
    }

    private String sanitizeReturnUrl(String returnUrl) {
        if (returnUrl == null || returnUrl.isBlank()) {
            return null;
        }

        String trimmed = returnUrl.trim();
        if (!trimmed.startsWith("/") || trimmed.startsWith("//")
                || trimmed.contains("\r") || trimmed.contains("\n")) {
            return null;
        }

        return trimmed;
    }
}
