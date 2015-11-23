package cz.muni.ics.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Replacement for Spring's RestTemplate that is available since Spring 3.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
public class RestTemplate {

    public <T> T postForObjectForm(String url, Map<String, String> parameters, Class<T> clazz) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        T value = null;
        try {
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
            httppost.setHeader("Accept", "application/json");
            httppost.setEntity(new UrlEncodedFormEntity(nvps));
            HttpEntity resEntity = httpclient.execute(httppost).getEntity();
            if (resEntity != null) {
                ObjectMapper mapper = new ObjectMapper();
                value = mapper.readValue(resEntity.getContent(), clazz);
            }
            EntityUtils.consume(resEntity);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return value;
    }


    public <T> T getForObject(String url, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = new DefaultHttpClient();
        T value = null;
        try {
            HttpGet request = new HttpGet(url);

            // add request header
            request.setHeader("Accept", "application/json");
            HttpEntity resEntity = client.execute(request).getEntity();
            if (resEntity != null) {
                value = mapper.readValue(resEntity.getContent(), clazz);
            }
            EntityUtils.consume(resEntity);
        } finally {
            client.getConnectionManager().shutdown();
        }
        return value;
    }
}
