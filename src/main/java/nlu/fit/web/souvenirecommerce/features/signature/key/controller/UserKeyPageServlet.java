package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        request.getRequestDispatcher("/WEB-INF/views/signature/user-key.jsp")
                .forward(request, response);
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
