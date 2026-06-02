package nlu.fit.web.souvenirecommerce.features.user.profile.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import nlu.fit.web.souvenirecommerce.common.utils.ApplicationLoader;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.features.product.service.CloudinaryService;
import nlu.fit.web.souvenirecommerce.core.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/user/upload-avatar/old")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class ChangeAvatarServlet extends HttpServlet {
    private static final String UPLOAD_DIR = ApplicationLoader.get("path.folder.avatar");

    @Override
    public void init() throws ServletException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()){
            boolean mkdir =  uploadDir.mkdirs();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userDto") == null) {
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

            byte[] fileBytes = filePart.getInputStream().readAllBytes();

            Map<String, String> uploadResult = CloudinaryService.uploadImage(fileBytes, "avatar");
            String newAvatarUrl = uploadResult.get("url");
            String newPublicId = uploadResult.get("public_id");

            if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
                try {
                    CloudinaryService.deleteImage(currentUser.getAvatarPublicId());
                } catch (Exception e) {
                    System.err.println("Failed to delete old avatar: " + e.getMessage());
                }
            }

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                User userToUpdate = hibernateSession.find(User.class, currentUser.getId());
                if (userToUpdate != null) {
                    userToUpdate.setAvatarUrl(newAvatarUrl);
                    userToUpdate.setAvatarPublicId(newPublicId);
                    hibernateSession.merge(userToUpdate);
                    transaction.commit();

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
