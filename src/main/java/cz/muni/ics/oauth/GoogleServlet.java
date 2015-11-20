package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * A servlet for OAuth2 login using Google account.
 * <p>
 * This application must be registered at Google APIs Console of the developer https://code.google.com/apis/console/
 * to receive client_id and client_secret. The redirect_uri must be registered there too.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/google/*")
public class GoogleServlet extends BaseOAuthServlet {

    @Override
    protected String getPrefix() {
        return "google";
    }

    protected String getLoginURL() {
        return "https://accounts.google.com/o/oauth2/auth";
    }

    protected String getScope() {
        return "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
    }

    protected String getTokenURL() {
        return "https://accounts.google.com/o/oauth2/token";
    }

    protected String getUserInfoURL(String token, HttpServletRequest req) {
        return "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + urlEncode(token);
    }

    @Override
    protected UserInfo getUserInfo(JsonNode userData, String token, HttpServletRequest req) {
        //{"id":"111085807076049784065","email":"martinkuba@gmail.com","verified_email":true,"name":"Martin Kuba","given_name":"Martin","family_name":"Kuba","link":"https://plus.google.com/111085807076049784065","picture":"https://lh6.googleusercontent.com/-xhJABfSEk7o/AAAAAAAAAAI/AAAAAAAAARY/F6FG931irCk/photo.jpg","gender":"male","locale":"cs"}
        String userId = userData.path("id").asText();
        String userEmail = userData.path("email").asText();
        String givenName = userData.path("given_name").asText();
        String surname = userData.path("family_name").asText();
        String userName = userData.path("name").asText();
        String userPicture = userData.path("picture").asText();
        return new UserInfo("Google", userId, userEmail, givenName, surname, userName, userPicture);
    }

}