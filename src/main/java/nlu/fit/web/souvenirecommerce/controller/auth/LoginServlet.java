package nlu.fit.web.souvenirecommerce.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.IUserDAO;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl2;
import nlu.fit.web.souvenirecommerce.model.User;
import nlu.fit.web.souvenirecommerce.model.entity.Role;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private UserDAOImpl userDAOImpl;
    private IUserDAO userEntityDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String loginDetail = req.getParameter("loginDetail");
        String password = req.getParameter("password");

        User user = userDAOImpl.login(loginDetail, password);
        if (user == null) {
            user = loginWithUserCredential(loginDetail, password);
        }

        if (user == null) {
            req.setAttribute("error", "Email, số điện thoại hoặc mật khẩu không đúng");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userInSession", user);
        session.setAttribute("user", user);
        session.setAttribute("authUser", user);

        Object redirectAfterLogin = session.getAttribute("redirectAfterLogin");
        if (redirectAfterLogin instanceof String redirect && !redirect.isBlank()) {
            session.removeAttribute("redirectAfterLogin");
            resp.sendRedirect(redirect);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    public void init() throws ServletException {
        userDAOImpl = new UserDAOImpl();
        userEntityDAO = new UserDAOImpl2();
    }

    private User loginWithUserCredential(String loginDetail, String password) {
        try {
            return userEntityDAO.findByUserEmailAndPassword(loginDetail, password)
                    .map(this::toSessionUser)
                    .orElse(null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private User toSessionUser(nlu.fit.web.souvenirecommerce.model.entity.User entityUser) {
        String fullName = (entityUser.getLastName() + " " + entityUser.getFirstName()).trim();
        String role = Optional.ofNullable(entityUser.getRoles())
                .filter(roles -> !roles.isEmpty())
                .map(roles -> roles.stream().map(Role::getName).collect(Collectors.joining(",")))
                .orElse("User");

        return User.builder()
                .id(entityUser.getId().intValue())
                .fullName(fullName)
                .email(entityUser.getEmail())
                .phone(entityUser.getPhone())
                .avatar(entityUser.getAvatarUrl())
                .status(entityUser.isActive() ? "Active" : "Inactive")
                .role(role)
                .createdAt(entityUser.getCreatedAt() == null ? null : entityUser.getCreatedAt().toString())
                .build();
    }
}
