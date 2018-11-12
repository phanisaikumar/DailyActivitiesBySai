package com.italent.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.italent.dao.LithiumMessageDAO;
import com.italent.utils.LithiumUtils;

public class ItalentUsersAndRoles {

    private static LithiumUtils lithiumUtils = null;
    public static final Logger logger = LogManager.getLogger(ItalentUsersAndRoles.class.getName());
    public static final String GET_SESSION_KEY_URL = "authentication/sessions/login?user.login=saikumarn&user.password=Q!w2e3r4&restapi.response_format=json";
    public static final String GETTING_USERS = "users?restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String USERS_COUNT = "users/count?&restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String GETTING_ROLES = "roles?restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String GETTING_ROLES_USERS = "roles/id/%ID%/users?restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    private static String DEMO_URL = null;

    public static void main(String[] args) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("application");
            DEMO_URL = bundle.getString("ITALENT_DEMO_URL");
            String accTokenUrl = DEMO_URL + GET_SESSION_KEY_URL;
            logger.info("Lithium session key api : " + accTokenUrl);
            lithiumUtils = new LithiumUtils();
            String accToken = lithiumUtils.generateSessionKey(accTokenUrl);
            int pageSize = Integer.valueOf(bundle.getString("PAGE_SIZE"));
            int usersCount = gettingUsersCount(accToken);
            int pageNum = usersCount / pageSize;
            for (int i = 0; i < pageNum; i++) {
                gettingUserList(accToken, pageSize, i + 1);
            }
            gettingRoles(accToken);
            logger.info("@@@@@@@@@@@@@@ completed successfully @@@@@@@@@@@@@");
        } catch (Exception e) {
            logger.error("error occured in ItalentUsersAndRoles main method() :" + e, e);
        }
    }

    private static void gettingRoles(String accToken) {
        try {
            String rolesUrl = DEMO_URL + GETTING_ROLES;
            rolesUrl = rolesUrl.replace("%SESSION_KEY%", accToken);
            HttpResponse rolesData = lithiumUtils.httpGetRequest(rolesUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeRoles = objectMapper.readTree(new BasicResponseHandler().handleResponse(rolesData));
            logger.info("User data : " + jsonNodeRoles);
            JsonNode rolesArray = jsonNodeRoles.get("response").get("roles").get("role");
            ArrayList<Integer> roleIds = LithiumMessageDAO.savingRolesDataInDataBase(rolesArray);
            logger.info(roleIds);
            savingUsersRolesDataInDataBase(roleIds, accToken);
        } catch (Exception e) {
            logger.error("gettingRoles() :" + e, e);
        }
    }

    private static void savingUsersRolesDataInDataBase(ArrayList<Integer> roleIds, String accToken)
            throws HttpResponseException, IOException {
        String rolesUserUrl = DEMO_URL + GETTING_ROLES_USERS;
        rolesUserUrl = rolesUserUrl.replace("%SESSION_KEY%", accToken);
        for (Integer roleId : roleIds) {
            String rolesUsersUrl = rolesUserUrl.replace("%ID%", roleId.toString());
            HttpResponse rolesUsersData = lithiumUtils.httpGetRequest(rolesUsersUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeRolesUsers = objectMapper
                    .readTree(new BasicResponseHandler().handleResponse(rolesUsersData));
            logger.info("User data : " + jsonNodeRolesUsers);
            JsonNode rolesArray = jsonNodeRolesUsers.get("response").get("users").get("user");
            LithiumMessageDAO.savingUsersRolesData(rolesArray, roleId);
        }

    }

    private static int gettingUsersCount(String accToken) throws HttpResponseException, IOException {
        String countUrl = DEMO_URL + USERS_COUNT;
        countUrl = countUrl.replace("%SESSION_KEY%", accToken);
        HttpResponse countData = lithiumUtils.httpGetRequest(countUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeCount = objectMapper.readTree(new BasicResponseHandler().handleResponse(countData));
        int count = jsonNodeCount.get("response").get("value").get("$").asInt();
        return count;
    }

    private static void gettingUserList(String sessionKey, int pageSize, int i) throws Exception {
        String userUrl = DEMO_URL + GETTING_USERS;
        userUrl = userUrl.replace("%SESSION_KEY%", sessionKey);
        userUrl = userUrl + "&page_size=" + pageSize + "&page=" + i;
        HttpResponse userData = lithiumUtils.httpGetRequest(userUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeUsers = objectMapper.readTree(new BasicResponseHandler().handleResponse(userData));
        logger.info("User data : " + jsonNodeUsers);
        JsonNode usersArray = jsonNodeUsers.get("response").get("users").get("user");
        logger.info("users list :" + usersArray);
        ArrayList<Integer> userList = LithiumMessageDAO.savingUsersDataInDataBase(usersArray);
        logger.info("successfully saved users : " + userList);
    }

}
