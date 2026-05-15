package nlu.fit.web.souvenirecommerce.controller.auth;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.IUserDAO;
import nlu.fit.web.souvenirecommerce.dao.impl.UserDAOImpl2;
import nlu.fit.web.souvenirecommerce.util.EmailUtil;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/api/signup", "/signup"})
public class SignupServlet extends HttpServlet {
    private IUserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl2();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/signup.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        JsonObject jsonResponse = new JsonObject();
        String email = EmailUtil.normalizeEmail(req.getParameter("email"));
        String firstName = normalize(req.getParameter("firstName"));
        String lastName = normalize(req.getParameter("lastName"));
        String phone = normalize(req.getParameter("phone"));
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        String validationError = validate(email, firstName, lastName, phone, password, confirmPassword);
        if (validationError != null) {
            writeJson(resp, jsonResponse, "error", validationError);
            return;
        }

        HttpSession session = req.getSession(false);
        String verifiedEmail = session == null ? null : (String) session.getAttribute("signupVerifiedEmail");
        if (!email.equals(verifiedEmail)) {
            writeJson(resp, jsonResponse, "error", "Vui lòng xác thực email trước khi đăng ký");
            return;
        }

        if (userDAO.hasEmailExist(email)) {
            writeJson(resp, jsonResponse, "error", "Email này đã được đăng ký");
            return;
        }

        try {
            boolean registered = userDAO.createUser(email, password, firstName, lastName, phone).isPresent();
            if (!registered) {
                writeJson(resp, jsonResponse, "error", "Không thể tạo tài khoản. Vui lòng thử lại");
                return;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            writeJson(resp, jsonResponse, "error", "Không thể tạo tài khoản. Vui lòng thử lại");
            return;
        }

        session.removeAttribute("signupEmail");
        session.removeAttribute("signupVerifiedEmail");

        writeJson(resp, jsonResponse, "success", "Tạo tài khoản thành công");

        // Create Session and setAttributes
    }

    private String validate(String email, String firstName, String lastName, String phone,
                            String password, String confirmPassword) {
        if (email == null || email.isEmpty()) {
            return "Email không được để trống";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Email không hợp lệ";
        }

        if (lastName == null || lastName.isBlank()) {
            return "Họ không được để trống";
        }

        if (firstName == null || firstName.isBlank()) {
            return "Tên không được để trống";
        }

        if (phone == null || !phone.matches("^[0-9]{10,20}$")) {
            return "Số điện thoại phải từ 10-20 chữ số";
        }

        if (password == null || password.length() < 8) {
            return "Mật khẩu phải ít nhất 8 ký tự";
        }

        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không trùng khớp";
        }

        return null;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void writeJson(HttpServletResponse resp, JsonObject jsonResponse, String status, String message) throws IOException {
        jsonResponse.addProperty("status", status);
        jsonResponse.addProperty("message", message);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse.toString());
    }
}
