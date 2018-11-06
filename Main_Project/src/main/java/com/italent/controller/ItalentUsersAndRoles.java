package com.italent.controller;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.italent.dao.LithiumMessageDAO;
import com.italent.model.Users;
import com.italent.utils.LithiumUtils;

public class ItalentUsersAndRoles {

    public static final Logger logger = LogManager.getLogger(ItalentUsersAndRoles.class.getName());

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        String accTokenUrl = prop.getProperty("GET_SESSION_KEY_URL");
        logger.info("Lithium session key api : " + accTokenUrl);
        LithiumUtils lithiumUtils = new LithiumUtils();
        String accToken = ItalentDemo.generateSessionKey(accTokenUrl, lithiumUtils);
        gettingUserList(accToken);
    }

    private static void gettingUserList(String sessionKey) throws Exception {
        LithiumUtils lithiumUtils = new LithiumUtils();
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        String userUrl = prop.getProperty("GETTING_USERS");
        userUrl = userUrl.replace("%SESSION_KEY%", sessionKey);
        HttpResponse userData = lithiumUtils.httpGetRequest(userUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeUsers = objectMapper.readTree(new BasicResponseHandler().handleResponse(userData));
        logger.info("User data : " + jsonNodeUsers);
        JsonNode usersArray = jsonNodeUsers.get("response").get("users").get("user");
        logger.info("users list :" + usersArray);
        savingUsersDataInDataBase(usersArray);
    }

    private static void savingUsersDataInDataBase(JsonNode usersArray) {
        Users users = new Users();
        for (JsonNode usersObj : usersArray) {
            users.setUserId(usersObj.get("id").get("$").asInt());
            users.setUserName(usersObj.get("login").get("$").asText());
            users.setUserEmail(usersObj.get("email").get("$").asText());
            int userId = LithiumMessageDAO.saveUsers(users);
            logger.info("successfully saved user id :" + userId);
        }

    }

}
