package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.annotation.WebServlet;

/**
 * Facebook OAuth. This "application" must be registered at  https://developers.facebook.com/apps/ and its
 * redirect URL must be specified there.
 * <p>
 * See https://developers.facebook.com/docs/howtos/login/server-side-login/
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/facebook/*")
public class FacebookServlet extends BaseOAuthServlet {

    @Override
    protected String getPrefix() {
        return "facebook";
    }

    protected String getLoginURL() {
        return "https://www.facebook.com/dialog/oauth";
    }

    protected String getScope() {
        return "email";
    }

    protected String getTokenURL() {
        return "https://graph.facebook.com/v2.3/oauth/access_token";
    }

    protected String getUserInfoURL(String token) {
        return "https://graph.facebook.com/me?access_token=" + urlEncode(token);
    }


    @Override
    protected UserInfo getUserInfo(JsonNode userData) {
        //{"id":"10208661916370953","email":"makub@ics.muni.cz","first_name":"Martin","gender":"male","last_name":"Kuba","link":"https://www.facebook.com/app_scoped_user_id/10208661916370953/","locale":"cs_CZ","name":"Martin Kuba","timezone":1,"updated_time":"2015-08-31T14:18:31+0000","verified":true}
        String userId = userData.path("id").asText();
        String userEmail = userData.path("email").asText();
        String givenName = userData.path("first_name").asText();
        String surname = userData.path("last_name").asText();
        String userName = userData.path("name").asText();
        String userPicture = "http://graph.facebook.com/" + userId + "/picture?type=large";
        return new UserInfo("Facebook", userId, userEmail, givenName, surname, userName, userPicture);
    }

}
