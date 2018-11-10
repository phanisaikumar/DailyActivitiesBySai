package com.italent.utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LithiumUtils {
    public static final Logger logger = LogManager.getLogger(LithiumUtils.class.getName());
    ResourceBundle bundle = null;

    public LithiumUtils() throws FileNotFoundException, IOException {
        bundle = ResourceBundle.getBundle("application");
    }

    public HttpResponse httpGetRequest(String accUrl) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(accUrl);
            String htaccHeaderValue = bundle.getString("HTACCESS_HEAEDER_VALUE");
            request.addHeader("Authorization", htaccHeaderValue);
            HttpResponse response = client.execute(request);
            logger.info("Response Code : " + response.getStatusLine().getStatusCode());
            return response;
        } catch (Exception e) {
            logger.error("httpGetRequest():" + e, e);
        }
        return null;
    }

    public HttpResponse HttpPostRequest(String createBoardUrl) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(createBoardUrl);
            String htaccHeaderValue = bundle.getString("HTACCESS_HEAEDER_VALUE");
            post.addHeader("Authorization", htaccHeaderValue);
            HttpResponse response = client.execute(post);
            logger.info("Response Code : " + response.getStatusLine().getStatusCode());
            return response;
        } catch (Exception e) {
            logger.error("HttpPostRequest():" + e, e);
        }
        return null;
    }
    
    public String generateSessionKey(String accTokenUrl) throws Exception {
        HttpResponse response = httpGetRequest(accTokenUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(response));
        String sessionKey = jsonNode.get("response").get("value").get("$").asText();
        logger.info("session key : " + sessionKey);
        return sessionKey;

    }
}
