package nlu.fit.web.souvenirecommerce.auth.service;

import nlu.fit.web.souvenirecommerce.auth.dto.GooglePojo;
import nlu.fit.web.souvenirecommerce.auth.dao.AuthDAO;
import nlu.fit.web.souvenirecommerce.auth.util.GoogleUtils;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;

public class AuthService {
    private AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthService() {
        this.authDAO = new AuthDAO();
    }

    public User loginWithUserCredential(String email, String password) {
        return authDAO.findByUserEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng."));
    }

    public GooglePojo processGoogleLogin(String code) throws IOException {
        if (code == null || code.isEmpty()){
            throw new IllegalArgumentException("Code is invalid");
        }

        String accessToken = GoogleUtils.getToken(code);

        GooglePojo googleUser = GoogleUtils.getUserInfo(accessToken);

        if (googleUser == null || googleUser.getId() == null) {
            throw new IllegalStateException("Google ID is null");
        }

        return googleUser;
    }

    public User loginWithGoogle(String code) throws IOException {
        GooglePojo googleUser = processGoogleLogin(code);
        return authDAO.upsertGoogleUser(
                googleUser.getId(),
                googleUser.getEmail(),
                googleUser.getGiven_name(),
                googleUser.getFamily_name(),
                googleUser.getPicture()
        );
    }

}
