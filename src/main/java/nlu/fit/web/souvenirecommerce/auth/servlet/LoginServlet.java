package nlu.fit.web.souvenirecommerce.auth.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import nlu.fit.web.souvenirecommerce.auth.dao.AuthDAO;
import nlu.fit.web.souvenirecommerce.auth.service.AuthService;
import nlu.fit.web.souvenirecommerce.model.entity.Role;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService(new AuthDAO());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }


    /**
     * Handles HTTP POST requests for user authentication. This method processes login credentials
     * submitted by the client, performs authentication via the AuthService, manages user session
     * security, and provides appropriate responses or redirections based on the authentication result.
     *
     * @param req  the HttpServletRequest object that contains the request the client has made
     *             of the servlet. It provides request information for HTTP servlets, such as parameter
     *             values and session management.
     * @param resp the HttpServletResponse object that contains the response the servlet sends to
     *             the client. It allows the servlet to send data back to the client or redirect the client.
     * @throws ServletException if an input or output error occurs during the processing of the request.
     * @throws IOException      if the request handling fails due to I/O errors.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String loginDetail = req.getParameter("loginDetail");
        String password = req.getParameter("password");

        try {
            User user = authService.loginWithUserCredential(loginDetail, password);

            HttpSession session = req.getSession(false);
            Object redirectAfterLogin = session == null ? null : session.getAttribute("redirectAfterLogin");
            if (session != null) {
                session.invalidate();
            }
            session = req.getSession(true);
            setAuthenticatedUser(session, user);
            log.info("Đăng nhập thành công: userId={}, loginDetail={}", user.getId(), loginDetail);

            if (redirectAfterLogin instanceof String redirect && !redirect.isBlank()) {
                resp.sendRedirect(redirect);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/home");

        } catch (IllegalArgumentException e) {
            log.warn("Đăng nhập thất bại cho tài khoản: {}", loginDetail);

            req.setAttribute("error", "Email, số điện thoại hoặc mật khẩu không đúng");

            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void setAuthenticatedUser(HttpSession session, User user) {
        session.setAttribute("currentUser", user);

        nlu.fit.web.souvenirecommerce.model.User legacyUser = toLegacySessionUser(user);
        session.setAttribute("userInSession", legacyUser);
        session.setAttribute("user", legacyUser);
        session.setAttribute("authUser", legacyUser);
    }

    private nlu.fit.web.souvenirecommerce.model.User toLegacySessionUser(User entityUser) {
        String role = Optional.ofNullable(entityUser.getRoles())
                .filter(roles -> !roles.isEmpty())
                .map(roles -> roles.stream().map(Role::getName).collect(Collectors.joining(",")))
                .orElse("Customer");

        return nlu.fit.web.souvenirecommerce.model.User.builder()
                .id(entityUser.getId() == null ? 0 : entityUser.getId().intValue())
                .fullName(entityUser.getFullName())
                .email(entityUser.getEmail())
                .phone(entityUser.getPhone())
                .avatar(entityUser.getAvatarUrl())
                .status(entityUser.isActive() ? "Active" : "Inactive")
                .role(role)
                .gender(entityUser.getGender())
                .dob(entityUser.getDateOfBirth() == null ? null : entityUser.getDateOfBirth().toString())
                .createdAt(entityUser.getCreatedAt() == null ? null : entityUser.getCreatedAt().toString())
                .build();
    }
}
