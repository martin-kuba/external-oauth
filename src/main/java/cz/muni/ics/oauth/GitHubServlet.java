package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * GitHub OAuth. https://developer.github.com/v3/oauth/
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/github/*")
public class GitHubServlet extends BaseOAuthServlet {
    @Override
    protected String getPrefix() {
        return "github";
    }

    @Override
    protected String getLoginURL() {
        return "https://github.com/login/oauth/authorize";
    }

    @Override
    protected String getScope() {
        return "user:email";
    }

    @Override
    protected String getTokenURL() {
        return "https://github.com/login/oauth/access_token";
    }

    @Override
    protected String getUserInfoURL(String token, HttpServletRequest req) {
        return "https://api.github.com/user?access_token="+urlEncode(token);
    }

    @Override
    protected UserInfo getUserInfo(JsonNode userData, String token, HttpServletRequest req) {
        String userId = userData.path("id").asText();

        String givenName = null;
        String surname = null;
        String userName = userData.path("name").asText();
        String userPicture = userData.path("avatar_url").asText();

        String userEmail;
        JsonNode email = userData.path("email");
        if(email.isNull()) {
            //no public address, we must ask a specific API
            JsonNode response = this.getForObject("https://api.github.com/user/emails?access_token="+ token);
            userEmail = response.get(0).path("email").asText();
        } else {
            userEmail = email.asText();
        }
        return new UserInfo("GitHub", userId, userEmail, givenName, surname, userName, userPicture);
    }
}
