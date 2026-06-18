package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.PublicKeyFingerprintService;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet("/api/user/keys/active")
public class ActiveUserKeyApiServlet extends HttpServlet {
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final UserKeyService userKeyService = new UserKeyService();
    private final PublicKeyFingerprintService fingerprintService = new PublicKeyFingerprintService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        User currentUser = requireCurrentUser(request, response);
        if (currentUser == null) {
            return;
        }

        writeActiveKey(response, currentUser.getId());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        User currentUser = requireCurrentUser(request, response);
        if (currentUser == null) {
            return;
        }

        try {
            JsonObject body = JsonParser.parseReader(request.getReader()).getAsJsonObject();
            String publicKey = body.has("publicKey") && !body.get("publicKey").isJsonNull()
                    ? body.get("publicKey").getAsString()
                    : null;
            userKeyService.saveNewPublicKey(currentUser.getId(), publicKey);
            writeActiveKey(response, currentUser.getId());
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không thể lưu public key lên web: " + e.getMessage());
        }
    }

    private User requireCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User currentUser = getCurrentUser(request.getSession(false));
        if (currentUser == null || currentUser.getId() == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Bạn cần đăng nhập để truy cập public key ACTIVE.");
            return null;
        }
        return currentUser;
    }

    private void writeActiveKey(HttpServletResponse response, Long userId) throws IOException {
        Optional<UserKeyDTO> activeKey = userKeyDAO.findActiveKeyByUserId(userId);
        if (activeKey.isEmpty()) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Tài khoản chưa có public key ACTIVE.");
            return;
        }

        UserKeyDTO key = activeKey.get();
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("keyId", key.getId());
        json.addProperty("publicKey", key.getPublicKey());
        json.addProperty("fingerprint", fingerprintService.sha256Fingerprint(key.getPublicKey()));
        json.addProperty("createdAt", key.getCreatedAt() == null
                ? ""
                : key.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(json.toString());
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        for (String attribute : new String[] {"userInSession", "user", "currentUser"}) {
            Object value = session.getAttribute(attribute);
            if (value instanceof User user) {
                return user;
            }
        }
        return null;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        response.setStatus(status);
        response.getWriter().write(json.toString());
    }
}
