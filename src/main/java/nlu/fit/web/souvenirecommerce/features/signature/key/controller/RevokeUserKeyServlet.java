package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;

@WebServlet("/signature/keys/revoke")
public class RevokeUserKeyServlet extends HttpServlet {
    private final UserKeyService userKeyService = new UserKeyService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Long keyId = Long.parseLong(request.getParameter("keyId"));
            userKeyService.revokeKey(keyId, currentUser.getId());
            request.getSession().setAttribute("success", "Thu hồi public key thành công.");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Không thể thu hồi public key.");
        }

        response.sendRedirect(request.getContextPath() + "/signature/keys");
    }
}