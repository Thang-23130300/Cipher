package nlu.fit.web.souvenirecommerce.auth.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.auth.service.AuthService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;

@WebServlet("/login-google")
public class LoginGoogleServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        try {
            User user = authService.loginWithGoogle(code);
            HttpSession session = req.getSession(false);
            Object redirectAfterLogin = session == null ? null : session.getAttribute("redirectAfterLogin");
            if (session != null) {
                session.invalidate();
            }
            session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("userInSession", user);
            session.setAttribute("user", user);
            session.setAttribute("authUser", user);
            if (redirectAfterLogin instanceof String redirect && !redirect.isBlank()) {
                resp.sendRedirect(redirect);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (Exception e) {
            req.getSession(true).setAttribute("error", "Đăng nhập Google thất bại.");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
