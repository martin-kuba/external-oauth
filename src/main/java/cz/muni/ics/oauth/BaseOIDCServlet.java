package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Base servlet for OpenID Connect.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
public abstract class BaseOIDCServlet extends BaseOAuthServlet {

	private String authorization_endpoint;
	private String userinfo_endpoint;
	private String token_endpoint;
	private String issuer;

	@Override
	public void init() throws ServletException {
		super.init();
		String prefix = this.getProviderPrefix();
		String metadataURL = getProperty(prefix + ".metadata");
		JsonNode jn = getForObject(metadataURL);
		authorization_endpoint = jn.path("authorization_endpoint").asText();
		userinfo_endpoint = jn.path("userinfo_endpoint").asText();
		token_endpoint = jn.path("token_endpoint").asText();
		issuer = jn.path("issuer").asText();
	}

	@Override
	protected String getLoginURL() {
		return authorization_endpoint;
	}

	@Override
	protected String getScope() {
		return "openid email profile address phone";
	}

	@Override
	protected String getTokenURL() {
		return token_endpoint;
	}

	@Override
	protected String getUserInfoURL(HttpServletRequest req) {
		return userinfo_endpoint;
	}

	@Override
	protected UserInfo getUserInfo(JsonNode userData, String token, HttpServletRequest req) {
		String userId = userData.path("sub").asText();
		String userEmail = userData.path("email").asText();
		String givenName = userData.path("given_name").asText();
		String surname = userData.path("family_name").asText();
		String userName = userData.path("name").asText();
		String userPicture = userData.path("picture").asText();
		return new UserInfo(issuer, userId, userEmail, givenName, surname, userName, userPicture);
	}
}
