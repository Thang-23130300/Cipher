package nlu.fit.web.souvenirecommerce.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl;
import nlu.fit.web.souvenirecommerce.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet(name = "ProfileController", value = "/user/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 5 * 1024 * 1024,         // 5MB
        maxRequestSize = 10 * 1024 * 1024      // 10MB
)
public class ProfileController extends HttpServlet {

    private final UserDAOImpl dao = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("userInSession") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Chỉ load danh sách địa chỉ để HIỂN THỊ
        request.setAttribute("listAddr", dao.getAddressesByUserId(user.getId()));

        request.getRequestDispatcher("/user/userprofile.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("userInSession") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }

        switch (action) {

            /* ================= UPDATE PROFILE ================= */
            case "update_profile" -> {
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String gender = request.getParameter("gender");
                String dob = request.getParameter("dob");

                if (dao.updateProfile(user.getId(), fullName, phone, gender, dob)) {
                    user.setFullName(fullName);
                    user.setPhone(phone);
                    user.setGender(gender);
                    user.setDob(dob);
                    session.setAttribute("userInSession", user);
                }
            }

            /* ================= CHANGE AVATAR ================= */
            case "change_avatar" -> {
                Part avatarPart = request.getPart("avatarFile");

                if (avatarPart != null && avatarPart.getSize() > 0) {

                    String fileName = Paths.get(avatarPart.getSubmittedFileName())
                            .getFileName().toString();

                    String newFileName = UUID.randomUUID() + "_" + fileName;

                    String uploadDir = request.getServletContext()
                            .getRealPath("/assets/image/Avatar");

                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    avatarPart.write(uploadDir + File.separator + newFileName);

                    dao.updateAvatar(user.getId(), newFileName);

                    user.setAvatar(newFileName);
                    session.setAttribute("userInSession", user);
                }
            }
            default -> {}
        }

        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
}
