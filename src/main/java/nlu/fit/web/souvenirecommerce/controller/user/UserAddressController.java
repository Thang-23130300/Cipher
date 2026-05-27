package nlu.fit.web.souvenirecommerce.controller.user;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;

@WebServlet("/user/address/*")
public class UserAddressController extends HttpServlet {

    private final UserDAOImpl dao = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("userInSession") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getPathInfo();
        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }

        try {
            switch (path) {

                case "/default" -> {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (user.getId() != null) dao.setDefaultAddress(user.getId().intValue(), id);
                }

                case "/delete" -> {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (user.getId() != null) dao.deleteAddress(id, user.getId().intValue()); // SIẾT USER
                }

                default -> {
                    // không làm gì
                }
            }
        } catch (NumberFormatException e) {
            // user sửa URL → bỏ qua
        }

        response.sendRedirect(request.getContextPath() + "/user/profile");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("userInSession") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getPathInfo();
        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }

        try {
            switch (path) {

                case "/add" -> {
                    if (user.getId() != null) dao.addAddress(
                            user.getId().intValue(),
                            request.getParameter("addressDetail"),
                            request.getParameter("city"),
                            request.getParameter("district"),
                            request.getParameter("ward")
                    );
                }

                case "/edit" -> {
                    // CHƯA DÙNG – giữ sẵn để gộp edit sau
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (user.getId() != null) dao.updateAddress(
                            id,
                            user.getId().intValue(),
                            request.getParameter("addressDetail"),
                            request.getParameter("ward"),
                            request.getParameter("district"),
                            request.getParameter("city")
                    );
                }

                default -> {
                    // không làm gì
                }
            }
        } catch (NumberFormatException e) {
            // user sửa URL → bỏ qua
        }

        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
}
