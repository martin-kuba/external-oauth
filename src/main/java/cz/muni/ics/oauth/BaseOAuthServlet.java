package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Random;

/**
 * Base servlet for OAuth 2 authentication. Its subclasses have to provide specific URLs.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
public abstract class BaseOAuthServlet extends HttpServlet {

    final static Logger log = LoggerFactory.getLogger(BaseOAuthServlet.class);

    public static final String USER = "user";


    protected abstract String getPrefix();

    protected abstract String getLoginURL();

    protected abstract String getScope();

    protected abstract String getTokenURL();

    protected abstract String getUserInfoURL(String token, HttpServletRequest req);

    protected abstract UserInfo getUserInfo(JsonNode userData, String token, HttpServletRequest req);

    protected String client_id;
    protected String client_secret;
    protected String redirect_uri;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
         * Step 1 - initiate auth
         */
        HttpSession session = req.getSession(true);
        if ("/login".equals(req.getPathInfo())) {
            //initiate OAuth2 authorization
            //state is a random value protecting against XSRF
            String state = Integer.toString(random.nextInt(Integer.MAX_VALUE));
            session.setAttribute("state", state);
            //redirect to ask for permissions
            String loginRedirectURL = getLoginURL() + "?" +
                    "response_type=code"
                    + "&client_id=" + urlEncode(client_id)
                    + "&redirect_uri=" + urlEncode(redirect_uri)
                    + "&state=" + urlEncode(state)
                    + "&scope=" + urlEncode(getScope());
            resp.sendRedirect(loginRedirectURL);
            log.debug("send redirect to initiate OAuth2 flow");
        } else if ("/auth".equals(req.getPathInfo())) {
            //check state for XSRF attack
            String state = req.getParameter("state");
            String state1 = (String) session.getAttribute("state");
            if (state == null || !state.equals(state1)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "state does not match, probably a XSRF attack");
                return;
            }
            //get code expressing user consent
            String code = req.getParameter("code");
            if (code == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "code not present");
                return;
            }
            log.debug("got code {}, going to exchange it for accessToken", code);

            //exchange code for token
            JsonNode tokenResponse = exchangeCodeForToken(code);
            log.debug("received access token response: {}", tokenResponse);
            String accessToken = tokenResponse.path("access_token").asText();
            this.tokenResponseHook(tokenResponse, req);
            log.debug("received accessToken {}", accessToken);

            //use token for getting user data
            log.debug("using accessToken to read user data");
            JsonNode userData = getForObject(getUserInfoURL(accessToken, req));
            log.debug("user data: {}", userData);

            UserInfo userInfo = getUserInfo(userData, accessToken, req);
            log.info("got user {}", userInfo);
            //store it
            session.setAttribute(USER, userInfo);
            //redirect to home page
            resp.sendRedirect(req.getContextPath() + "/show");
        }
    }

    protected JsonNode exchangeCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("client_id", client_id);
        map.set("client_secret", client_secret);
        map.set("redirect_uri", redirect_uri);
        map.set("code", code);
        map.set("grant_type", "authorization_code");
        try {
            return restTemplate.postForObject(getTokenURL(), map, JsonNode.class);
        } catch (HttpClientErrorException ex) {
            log.error("HttpClientErrorException - HTTP POST failed", ex);
            log.error("response body: {}", ex.getResponseBodyAsString());
            throw ex;
        } catch (RestClientException ex) {
            log.error("RestClientException - HTTP POST failed", ex);
            throw ex;
        }
    }

    protected void tokenResponseHook(JsonNode tokenResponse, HttpServletRequest req) {
    }

    private Random random = new Random();

    private static String getProperty(Properties p, String name) throws ServletException {
        String value = p.getProperty(name);
        if (value == null) throw new ServletException("property " + name + " not defined in config.properties");
        return value;
    }

    @Override
    public void init() throws ServletException {
        try {
            Properties p = new Properties();
            p.load(this.getClass().getResourceAsStream("/config.properties"));
            String prefix = this.getPrefix();
            client_id = getProperty(p, prefix + ".client_id");
            client_secret = getProperty(p, prefix + ".client_secret");
            String url_prefix = getProperty(p, "url.prefix");
            redirect_uri = url_prefix + getPrefix() + "/auth";
        } catch (IOException e) {
            throw new ServletException("cannot read file config.properties", e);
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("utf-8 unknown", e);
        }
    }

    protected JsonNode getForObject(String url) {
        JsonNode response = new RestTemplate().getForObject(url, JsonNode.class);
        log.debug("getForObject() response={}",response);
        return response;
    }
}
