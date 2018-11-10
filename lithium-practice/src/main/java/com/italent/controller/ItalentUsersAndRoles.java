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
import com.italent.model.Roles;
import com.italent.model.UserRoles;
import com.italent.model.Users;
import com.italent.utils.LithiumUtils;

public class ItalentUsersAndRoles {

    private static LithiumUtils lithiumUtils = null;
    public static final Logger logger = LogManager.getLogger(ItalentUsersAndRoles.class.getName());
    public static final String GET_SESSION_KEY_URL = "authentication/sessions/login?user.login=saikumarn&user.password=Q!w2e3r4&restapi.response_format=json";
    public static final String GETTING_USERS = "users?restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String USERS_COUNT = "users/count?&restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String GETTING_ROLES = "users/id/%USER_ID%/roles?restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
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
            int pageNum = usersCount/pageSize;
            for(int i=0;i<pageNum;i++) {
            gettingUserList(accToken,pageSize,i+1);
            }
            logger.info("@@@@@@@@@@@@@@ completed successfully @@@@@@@@@@@@@");
        } catch (Exception e) {
            logger.error("error occured in ItalentUsersAndRoles main method() :" + e, e);
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
        userUrl =userUrl+"&page_size="+pageSize+"&page="+i;
        HttpResponse userData = lithiumUtils.httpGetRequest(userUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeUsers = objectMapper.readTree(new BasicResponseHandler().handleResponse(userData));
        logger.info("User data : " + jsonNodeUsers);
        JsonNode usersArray = jsonNodeUsers.get("response").get("users").get("user");
        logger.info("users list :" + usersArray);
        ArrayList<Integer> userList = savingUsersDataInDataBase(usersArray);
        String rolesUrl = DEMO_URL + GETTING_ROLES;
        //gettingRolesList(userList, sessionKey, rolesUrl);
    }

    private static ArrayList<Integer> savingUsersDataInDataBase(JsonNode usersArray) {
        Users users = new Users();
        ArrayList<Integer> userList = new ArrayList<Integer>();
        for (JsonNode usersObj : usersArray) {
            users.setUserId(usersObj.get("id").get("$").asInt());
            users.setUserName(usersObj.get("login").get("$").asText());
            users.setUserEmail(usersObj.get("email").get("$").asText());
            int userId = LithiumMessageDAO.saveUsers(users);
            logger.info("successfully saved user id :" + userId);
            userList.add(userId);
        }
        return userList;

    }

    private static void gettingRolesList(ArrayList<Integer> userList, String sessionKey, String rolesUrl)
            throws Exception {
        rolesUrl = rolesUrl.replace("%SESSION_KEY%", sessionKey);
        for (int list : userList) {
            String roleUrl = rolesUrl.replace("%USER_ID%", Integer.toString(list));
            logger.info("roles url :" + roleUrl);
            HttpResponse rolesDataResp = lithiumUtils.httpGetRequest(roleUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeRoles = objectMapper.readTree(new BasicResponseHandler().handleResponse(rolesDataResp));
            logger.info("Roles data : " + jsonNodeRoles);
            JsonNode rolesArray = jsonNodeRoles.get("response").get("roles").get("role");
            logger.info("roles list :" + rolesArray);
            savingRolesDataInDataBase(rolesArray, list);
        }
    }

    private static void savingRolesDataInDataBase(JsonNode rolesArray, int list) {
        Roles roles = new Roles();
        UserRoles userRoles = new UserRoles();
        if (rolesArray.size() != 0) {
            for (JsonNode rolesObj : rolesArray) {
                roles.setRoleId(rolesObj.get("id").get("$").asInt());
                roles.setRoleName(rolesObj.get("name").get("$").asText());
                userRoles.setUserId(list);
                userRoles.setRoleId(rolesObj.get("id").get("$").asInt());
                logger.info("userRoles obj :" + userRoles);
                LithiumMessageDAO.saveUserRoles(userRoles);
                int roleId = LithiumMessageDAO.saveRoles(roles);
                logger.info("successfully saved role id :" + roleId);
            }
        }
    }

}
