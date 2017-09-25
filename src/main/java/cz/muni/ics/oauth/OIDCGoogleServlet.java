package cz.muni.ics.oauth;

import javax.servlet.annotation.WebServlet;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/oidc_google/*")
public class OIDCGoogleServlet extends BaseOIDCServlet{

	@Override
	protected String getProviderPrefix() {
		return "oidc_google";
	}

}
