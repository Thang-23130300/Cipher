package nlu.fit.web.souvenirecommerce.account.servlet;

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

@WebServlet(name = "ProfileController", value = "/user/profile" )
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
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

        request.setAttribute("pageTitle", "Hồ sơ");
        request.setAttribute("pageCss", "user/profile.css");
        request.setAttribute("pageJs", "user/profile.js");
        request.setAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");

        request.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
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
                    session.setAttribute("user", user);
                    session.setAttribute("profileMessage", "Cập nhật thông tin thành công.");
                    session.setAttribute("profileMessageType", "success");
                } else {
                    session.setAttribute("profileMessage", "Không thể cập nhật thông tin. Vui lòng kiểm tra lại dữ liệu.");
                    session.setAttribute("profileMessageType", "error");
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
                            .getRealPath("/assets/images/Avatar");

                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    avatarPart.write(uploadDir + File.separator + newFileName);

                    if (dao.updateAvatar(user.getId(), newFileName)) {
                        user.setAvatar(newFileName);
                        session.setAttribute("userInSession", user);
                        session.setAttribute("user", user);
                        session.setAttribute("profileMessage", "Cập nhật ảnh đại diện thành công.");
                        session.setAttribute("profileMessageType", "success");
                    } else {
                        session.setAttribute("profileMessage", "Không thể cập nhật ảnh đại diện.");
                        session.setAttribute("profileMessageType", "error");
                    }
                }
            }
            default -> {}
        }

        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
}
