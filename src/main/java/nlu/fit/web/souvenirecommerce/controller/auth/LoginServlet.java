package nlu.fit.web.souvenirecommerce.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl;
import nlu.fit.web.souvenirecommerce.model.User;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private UserDAOImpl userDAOImpl;

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
    }
}