package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * OrcID OAuth 2.
 * Doc at
 * <ul>
 *     <li>http://members.orcid.org/api/introduction-orcid-public-api</li>
 *     <li>http://members.orcid.org/api/accessing-public-api</li>
 * </ul>
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/orcid/*")
public class OrcidServlet extends BaseOAuthServlet {

    @Override
    protected String getPrefix() {
        return "orcid";
    }

    @Override
    protected String getLoginURL() {
        // http://members.orcid.org/api/get-oauthauthorize
        return "http://orcid.org/oauth/authorize";
    }

    @Override
    protected String getScope() {
        // http://members.orcid.org/api/orcid-scopes
        return "/authenticate";
    }

    @Override
    protected String getTokenURL() {
        return "http://pub.orcid.org/oauth/token";
    }

    @Override
    protected void tokenResponseHook(JsonNode tokenResponse, HttpServletRequest req) {
        req.setAttribute("orcid", tokenResponse.path("orcid").asText());
        req.setAttribute("name", tokenResponse.path("name").asText());
    }

    @Override
    protected String getUserInfoURL(String token, HttpServletRequest req) {
        // http://members.orcid.org/api/tutorial-retrieve-data-public-api-curl-12-and-earlier
        return "http://pub.orcid.org/v1.2/"+ req.getAttribute("orcid");
    }

    @Override
    protected UserInfo getUserInfo(JsonNode userData, String token, HttpServletRequest req) {
        JsonNode orcid_bio = userData.path("orcid-profile").path("orcid-bio");

        String userId = req.getAttribute("orcid").toString();

        JsonNode email = orcid_bio.path("contact-details").path("email");

        String userEmail = (email.size()>0) ? email.get(0).path("value").asText() : null;

        JsonNode personal_details = orcid_bio.path("personal-details");
        String givenName = personal_details.path("given-names").path("value").asText();
        String surname = personal_details.path("family-name").path("value").asText();

        String userName = req.getAttribute("name").toString();

        return new UserInfo("Orcid", userId, userEmail, givenName, surname, userName, null);
    }
}
