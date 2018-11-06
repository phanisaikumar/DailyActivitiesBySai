package com.italent.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LithiumUtils {
    public static final Logger logger = LogManager.getLogger(LithiumUtils.class.getName());
    
    public HttpResponse httpGetRequest(String accUrl) throws ClientProtocolException, IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(accUrl);
        String htaccHeaderValue = prop.getProperty("HTACCESS_HEAEDER_VALUE");
        request.addHeader("Authorization", htaccHeaderValue);
        HttpResponse response = client.execute(request);
        logger.info("Response Code : " + response.getStatusLine().getStatusCode());
        return response;
    }
    public HttpResponse HttpPostRequest(String createBoardUrl) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(createBoardUrl);
        String htaccHeaderValue = prop.getProperty("HTACCESS_HEAEDER_VALUE");
        post.addHeader("Authorization", htaccHeaderValue);
        HttpResponse response = client.execute(post);
        logger.info("Response Code : " + response.getStatusLine().getStatusCode());
        return response;
    }
}
