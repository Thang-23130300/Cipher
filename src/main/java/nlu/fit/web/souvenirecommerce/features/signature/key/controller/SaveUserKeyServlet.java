package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PublicKeyFingerprintService fingerprintService = new PublicKeyFingerprintService();

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
            userKeyService.saveNewPublicKey(currentUser.getId(), publicKey);
            UserKeyDTO activeKey = userKeyService.getActiveKey(currentUser.getId())
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy public key ACTIVE vừa lưu."));

            request.getSession().setAttribute("success",
                    "Đã lưu public key trên web. Đang đồng bộ với Signing Tool...");
            request.getSession().setAttribute("toolSyncPending", Boolean.TRUE);
            request.getSession().setAttribute("toolSyncKeyId", activeKey.getId());
            request.getSession().setAttribute("toolSyncPublicKey", activeKey.getPublicKey());
            request.getSession().setAttribute("toolSyncFingerprint",
                    fingerprintService.sha256Fingerprint(activeKey.getPublicKey()));
            request.getSession().setAttribute("toolSyncCreatedAt",
                    activeKey.getCreatedAt() == null ? "" : activeKey.getCreatedAt().toString());

            String redirectUrl = request.getContextPath() + "/key-management";
            if (returnUrl != null) {
                redirectUrl += "?returnUrl=" + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8);
            }
            response.sendRedirect(redirectUrl);
            return;
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
