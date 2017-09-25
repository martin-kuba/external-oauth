package cz.muni.ics.oauth;

import javax.servlet.annotation.WebServlet;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/oidc_ms/*")
public class OIDCMicrosoftServlet extends BaseOIDCServlet{

	@Override
	protected String getProviderPrefix() {
		return "oidc_ms";
	}

}
