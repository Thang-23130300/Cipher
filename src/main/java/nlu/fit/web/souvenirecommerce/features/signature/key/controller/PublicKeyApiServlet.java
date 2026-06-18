package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.auth.repository.AuthRepository;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/api/public-key")
public class PublicKeyApiServlet extends HttpServlet {
    private final AuthRepository authRepository = new AuthRepository();
    private final UserKeyService userKeyService = new UserKeyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Email không được để trống");
            out.print(jsonResponse.toString());
            return;
        }

        try {
            Optional<User> optUser = authRepository.findByUserEmail(email.trim());
            if (optUser.isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Không tìm thấy người dùng ứng với email đã cung cấp");
                out.print(jsonResponse.toString());
                return;
            }

            User user = optUser.get();
            Optional<UserKeyDTO> optActiveKey = userKeyService.getActiveKey(user.getId());
            if (optActiveKey.isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Không tìm thấy khóa công khai (Public Key) đang hoạt động");
                out.print(jsonResponse.toString());
                return;
            }

            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("email", user.getEmail());
            jsonResponse.addProperty("publicKey", optActiveKey.get().getPublicKey());
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Lỗi hệ thống: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }
}
