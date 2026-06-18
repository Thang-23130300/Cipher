package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.common.utils.ApplicationLoader;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/signature/keys", "/key-management"})
public class UserKeyPageServlet extends HttpServlet {
    private final UserKeyService userKeyService = new UserKeyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = currentUser.getId();

        Optional<UserKeyDTO> activeKey = userKeyService.getActiveKey(userId);
        List<UserKeyDTO> keyHistory = userKeyService.getUserKeys(userId);

        request.setAttribute("activeKey", activeKey.orElse(null));
        request.setAttribute("keyHistory", keyHistory);
        request.setAttribute("returnUrl", sanitizeReturnUrl(request.getParameter("returnUrl")));
        moveToolSyncPayloadToRequest(request);
        request.setAttribute("signingToolPort", getSigningToolPort());

        request.getRequestDispatcher("/WEB-INF/views/signature/user-key.jsp")
                .forward(request, response);
    }

    private void moveToolSyncPayloadToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!Boolean.TRUE.equals(session.getAttribute("toolSyncPending"))) {
            return;
        }

        request.setAttribute("toolSyncPending", Boolean.TRUE);
        for (String name : new String[] {
                "toolSyncKeyId", "toolSyncPublicKey", "toolSyncFingerprint", "toolSyncCreatedAt"
        }) {
            request.setAttribute(name, session.getAttribute(name));
            session.removeAttribute(name);
        }
        session.removeAttribute("toolSyncPending");
    }

    private int getSigningToolPort() {
        String configuredPort = ApplicationLoader.getProperties()
                .getProperty("signing.tool.port", "9090");
        try {
            int port = Integer.parseInt(configuredPort.trim());
            return port >= 1 && port <= 65535 ? port : 9090;
        } catch (NumberFormatException e) {
            return 9090;
        }
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
