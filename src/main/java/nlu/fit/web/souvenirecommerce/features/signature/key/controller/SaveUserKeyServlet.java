package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/signature/keys/save")
public class SaveUserKeyServlet extends HttpServlet {
    private final UserKeyService userKeyService = new UserKeyService();

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
            request.getSession().setAttribute("success", "Lưu public key thành công.");
            response.sendRedirect(request.getContextPath()
                    + (returnUrl == null ? "/key-management" : returnUrl));
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
