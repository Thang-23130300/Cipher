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

@WebServlet("/signature/keys/verify-otp")
public class VerifyUserKeyOtpServlet extends HttpServlet {
    private final KeyOtpService keyOtpService = new KeyOtpService();
    private final UserKeyService userKeyService = new UserKeyService();
    private final PublicKeyFingerprintService fingerprintService = new PublicKeyFingerprintService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String returnUrl = sanitizeReturnUrl(request.getParameter("returnUrl"));

        if ("cancel".equals(action)) {
            // Hủy bỏ luồng đổi key và dọn dẹp session
            request.getSession().removeAttribute("keyChangePending");
            request.getSession().removeAttribute("keyChangeReturnUrl");
            request.getSession().setAttribute("info", "Đã hủy yêu cầu cập nhật Public Key.");

            response.sendRedirect(request.getContextPath() + "/key-management"
                    + (returnUrl != null ? "?returnUrl=" + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8) : ""));
            return;
        }

        String otpCode = request.getParameter("otpCode");

        try {
            boolean isOtpValid = keyOtpService.verifyOtp(currentUser.getId(), otpCode);
            if (isOtpValid) {
                // OTP chính xác -> tiến hành kích hoạt khóa mới và thu hồi khóa cũ
                keyOtpService.consumeOtpAndSaveKey(currentUser.getId());

                // Kích hoạt đồng bộ hóa với Signing Tool
                try {
                    userKeyService.getActiveKey(currentUser.getId()).ifPresent(activeKey -> {
                        request.getSession().setAttribute("toolSyncPending", Boolean.TRUE);
                        request.getSession().setAttribute("toolSyncKeyId", activeKey.getId());
                        request.getSession().setAttribute("toolSyncPublicKey", activeKey.getPublicKey());
                        request.getSession().setAttribute("toolSyncFingerprint",
                                fingerprintService.sha256Fingerprint(activeKey.getPublicKey()));
                        request.getSession().setAttribute("toolSyncCreatedAt",
                                activeKey.getCreatedAt() == null ? "" : activeKey.getCreatedAt().toString());
                    });
                } catch (Exception ignored) {}

                request.getSession().removeAttribute("keyChangePending");
                String finalReturnUrl = (String) request.getSession().getAttribute("keyChangeReturnUrl");
                request.getSession().removeAttribute("keyChangeReturnUrl");

                request.getSession().setAttribute("success", "Cập nhật Public Key mới thành công và đang tiến hành đồng bộ với Signing Tool.");
                response.sendRedirect(request.getContextPath()
                        + (finalReturnUrl != null ? finalReturnUrl : "/key-management"));
                return;
            } else {
                request.getSession().setAttribute("error", "Mã OTP không chính xác.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/key-management"
                + (returnUrl != null ? "?returnUrl=" + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8) : ""));
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
