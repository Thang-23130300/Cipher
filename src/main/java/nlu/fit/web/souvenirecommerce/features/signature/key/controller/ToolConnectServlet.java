package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.PublicKeyFingerprintService;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@WebServlet("/tool/connect")
public class ToolConnectServlet extends HttpServlet {
    private final UserKeyService userKeyService = new UserKeyService();
    private final PublicKeyFingerprintService fingerprintService = new PublicKeyFingerprintService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        User currentUser = getCurrentUser(request.getSession(false));
        if (currentUser == null || currentUser.getId() == null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", currentRequestUrl(request));
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String callbackUrl = trim(request.getParameter("callback"));
        String nonce = trim(request.getParameter("nonce"));
        if (!isAllowedCallback(callbackUrl)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Callback chỉ được phép trỏ tới localhost hoặc 127.0.0.1.");
            return;
        }
        if (nonce == null || nonce.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nonce không hợp lệ.");
            return;
        }

        Optional<UserKeyDTO> activeKey = userKeyService.getActiveKey(currentUser.getId());
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("callbackUrl", callbackUrl);
        request.setAttribute("nonce", nonce);
        request.setAttribute("activeKey", activeKey.orElse(null));
        if (activeKey.isPresent()) {
            request.setAttribute("fingerprint",
                    fingerprintService.sha256Fingerprint(activeKey.get().getPublicKey()));
        }

        request.getRequestDispatcher("/WEB-INF/views/signature/tool-connect.jsp")
                .forward(request, response);
    }

    static boolean isAllowedCallback(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(callbackUrl);
            String host = uri.getHost();
            return uri.isAbsolute()
                    && "http".equalsIgnoreCase(uri.getScheme())
                    && ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        for (String name : new String[] {"userInSession", "user", "currentUser", "authUser", "userDto"}) {
            Object value = session.getAttribute(name);
            if (value instanceof User user) {
                return user;
            }
        }
        return null;
    }

    private String currentRequestUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        return request.getRequestURI() + (query == null || query.isBlank() ? "" : "?" + query);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
