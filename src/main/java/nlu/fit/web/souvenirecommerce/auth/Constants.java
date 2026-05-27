package nlu.fit.web.souvenirecommerce.auth;

import nlu.fit.web.souvenirecommerce.util.ApplicationLoader;

public class Constants {
    public static final String GOOGLE_CLIENT_ID = ApplicationLoader.get("CLIENT_ID") + ".apps.googleusercontent.com";
    public static final String GOOGLE_CLIENT_SECRET = ApplicationLoader.get("CLIENT_SECRET");
    public static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/login-google";
    public static final String GOOGLE_LINK_GET_TOKEN = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_LINK_GET_USER_INFO = "https://www.googleapis.com/oauth2/v3/userinfo";


}