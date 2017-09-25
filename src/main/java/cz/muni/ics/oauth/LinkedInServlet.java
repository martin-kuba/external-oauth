package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Register this application at  https://www.linkedin.com/secure/developer
 * https://developer.linkedin.com/documents/authentication
 * https://developer.linkedin.com/documents/profile-api
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/linkedin/*")
public class LinkedInServlet extends BaseOAuthServlet {

    @Override
    protected String getProviderPrefix() {
        return "linkedin";
    }

    protected String getLoginURL() {
        return "https://www.linkedin.com/oauth/v2/authorization";
    }

    protected String getScope() {
        return "r_basicprofile r_emailaddress";
    }

    protected String getTokenURL() {
        return "https://www.linkedin.com/oauth/v2/accessToken";
    }

    protected String getUserInfoURL(HttpServletRequest req) {
        return "https://api.linkedin.com/v1/people/~?format=json";
    }


    @Override
    protected UserInfo getUserInfo(JsonNode userData, String token, HttpServletRequest req) {

        //{"emailAddress":"makub@ics.muni.cz","firstName":"Martin","id":"GQ83M3GKek","lastName":"Kuba","pictureUrl":"https://media.licdn.com/mpr/mprx/0_KMzyHnGFxm1-2PWo-yB_HzC5xu5K2P4o-R3iHztv8Wl81cfEpxLKQv6UlGL77NJ6lscCFt8d-s0N"}
        String userId = userData.path("id").asText();
        String userEmail = userData.path("emailAddress").asText();
        String givenName = userData.path("firstName").asText();
        String surname = userData.path("lastName").asText();
        String userPicture = userData.path("pictureUrl").asText();
        return new UserInfo(getProviderPrefix(), userId, userEmail, givenName, surname, givenName + " " + surname, userPicture);
    }

}
