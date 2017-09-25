package cz.muni.ics.oauth;

import javax.servlet.annotation.WebServlet;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet("/oidc_mitreid/*")
public class OIDCMitreIDServlet extends BaseOIDCServlet{

	@Override
	protected String getProviderPrefix() {
		return "oidc_mitreid";
	}

}
