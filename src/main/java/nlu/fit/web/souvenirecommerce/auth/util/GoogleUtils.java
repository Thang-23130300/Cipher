package nlu.fit.web.souvenirecommerce.auth.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nlu.fit.web.souvenirecommerce.auth.Constants;
import nlu.fit.web.souvenirecommerce.auth.dto.GooglePojo;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class GoogleUtils {

    /**
     * Dùng code nhận từ Google đổi lấy accessToken
     * @param code
     * @return accessToken
     * @throws IOException
     */
    public static String getToken(final String code) throws IOException {
        String response = Request.Post(Constants.GOOGLE_LINK_GET_TOKEN)
                .bodyForm(Form.form()
                        .add("client_id", Constants.GOOGLE_CLIENT_ID)
                        .add("client_secret", Constants.GOOGLE_CLIENT_SECRET)
                        .add("redirect_uri", Constants.GOOGLE_REDIRECT_URI)
                        .add("code", code)
                        .add("grant_type", "authorization_code")
                        .build())
                .execute().returnContent().asString();
        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        return jobj.get("access_token").toString().replace("\"", "");
    }

    /**
     * Dùng accessToken để lấy thông tin chi tiết của User
     * @param accessToken
     * @return GooglePojo
     * @throws IOException
     */
    public static GooglePojo getUserInfo(final String accessToken) throws IOException {
        String link = Constants.GOOGLE_LINK_GET_USER_INFO + "?access_token=" + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();

        return new Gson().fromJson(response, GooglePojo.class);
    }
}