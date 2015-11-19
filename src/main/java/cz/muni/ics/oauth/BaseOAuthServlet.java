package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
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

    protected abstract String getUserInfoURL(String token);

    protected abstract UserInfo getUserInfo(JsonNode userData);

    protected String client_id;
    protected String client_secret;
    protected String redirect_uri;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
         * Step 1 - initiate auth
         */
        if ("/login".equals(req.getPathInfo())) {
            //initiate OAuth2 authorization
            //state is a random value protecting against XSRF
            String state = Integer.toString(random.nextInt(Integer.MAX_VALUE));
            req.getSession(true).setAttribute("state", state);
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
            String state1 = (String) req.getSession(true).getAttribute("state");
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
            log.debug("got code, going to exchange it for accessToken");

            //exchange code for token
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.set("client_id", client_id);
            map.set("client_secret", client_secret);
            map.set("redirect_uri", redirect_uri);
            map.set("code", code);
            map.set("grant_type", "authorization_code");
            JsonNode jsonNode = restTemplate.postForObject(getTokenURL(), map, JsonNode.class);
            String accessToken = jsonNode.path("access_token").asText();
            String expires = jsonNode.path("expires_in").asText();
            log.debug("received accessToken {}, it expires in {} seconds", accessToken, expires);

            //use token for getting user data
            log.debug("using accessToken to read user data");
            JsonNode userData = restTemplate.getForObject(getUserInfoURL(accessToken), JsonNode.class);
            log.debug("user data: {}", userData);

            UserInfo userInfo = getUserInfo(userData);
            log.info("got user {}", userInfo);
            //store it
            req.getSession(true).setAttribute(USER, userInfo);
            //redirect to home page
            resp.sendRedirect(req.getContextPath() + "/show");
        }
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
            redirect_uri = getProperty(p, prefix + ".redirect_uri");
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
}
