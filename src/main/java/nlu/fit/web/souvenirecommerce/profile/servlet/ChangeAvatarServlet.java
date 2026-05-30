package nlu.fit.web.souvenirecommerce.profile.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.service.CloudinaryService;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/user/account/change-avatar")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class ChangeAvatarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        Part filePart = null;

        try {
            filePart = req.getPart("avatarUrlFile");

            if (filePart == null || filePart.getSize() == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Vui lòng chọn file ảnh\"}");
                return;
            }

            // Get file bytes
            byte[] fileBytes = filePart.getInputStream().readAllBytes();

            // Upload to Cloudinary
            Map<String, String> uploadResult = CloudinaryService.uploadImage(fileBytes, "avatar");
            String newAvatarUrl = uploadResult.get("url");
            String newPublicId = uploadResult.get("public_id");

            // Delete old avatar if exists
            if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
                try {
                    CloudinaryService.deleteImage(currentUser.getAvatarPublicId());
                } catch (Exception e) {
                    System.err.println("Failed to delete old avatar: " + e.getMessage());
                }
            }

            // Update User entity in database
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                User userToUpdate = hibernateSession.get(User.class, currentUser.getId());
                if (userToUpdate != null) {
                    userToUpdate.setAvatarUrl(newAvatarUrl);
                    userToUpdate.setAvatarPublicId(newPublicId);
                    hibernateSession.merge(userToUpdate);
                    transaction.commit();

                    // Update session user
                    currentUser.setAvatarUrl(newAvatarUrl);
                    currentUser.setAvatarPublicId(newPublicId);
                    session.setAttribute("currentUser", currentUser);

                    resp.setContentType("application/json");
                    resp.getWriter().write("{" +
                            "\"success\": true, " +
                            "\"message\": \"Thay đổi ảnh đại diện thành công\", " +
                            "\"avatarUrl\": \"" + newAvatarUrl + "\"" +
                            "}");
                }
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                hibernateSession.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Lỗi khi tải ảnh: " + e.getMessage() + "\"}");
        }
    }
}
