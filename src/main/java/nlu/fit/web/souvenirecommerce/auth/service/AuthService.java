package nlu.fit.web.souvenirecommerce.auth.service;

import nlu.fit.web.souvenirecommerce.auth.dao.AuthDAO;
import nlu.fit.web.souvenirecommerce.model.entity.User;

public class AuthService {
    private AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public User loginWithUserCredential(String email, String password) {
        return authDAO.findByUserEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng."));
    }
}
