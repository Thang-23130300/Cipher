package nlu.fit.web.souvenirecommerce.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.UserDAO;
import nlu.fit.web.souvenirecommerce.model.User;

import java.io.IOException;

@WebServlet("/user/change-password")
public class ChangePassController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null)
                ? (User) session.getAttribute("userInSession")
                : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/user/userchange-pas.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null)
                ? (User) session.getAttribute("userInSession")
                : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!userDAO.checkPassword(user.getId(), currentPassword)) {
            request.setAttribute("error", "Mật khẩu hiện tại không đúng");
        } else if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
        } else if (newPassword.length() < 8) {
            request.setAttribute("error", "Mật khẩu mới phải từ 8 ký tự trở lên");
        } else {

            boolean updated = userDAO.updatePassword(user.getId(), newPassword);

            if (updated) {
                request.setAttribute("success", "Đổi mật khẩu thành công");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại");
            }
        }

        request.getRequestDispatcher("/user/userchange-pas.jsp")
                .forward(request, response);
    }
}
