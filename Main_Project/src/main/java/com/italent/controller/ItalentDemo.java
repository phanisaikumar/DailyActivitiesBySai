package com.italent.controller;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.italent.dao.LithiumMessageDAO;
import com.italent.model.LithiumMessage;
import com.italent.utils.LithiumUtils;

public class ItalentDemo {
    public static final Logger logger = LogManager.getLogger(ItalentDemo.class.getName());
    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        String accTokenUrl = prop.getProperty("GET_SESSION_KEY_URL");
        logger.info("Lithium session key api : " + accTokenUrl);
        LithiumUtils lithiumUtils = new LithiumUtils();
        String accToken = generateSessionKey(accTokenUrl, lithiumUtils);
        String checkBoard = checkingBoard(lithiumUtils, accToken);
        if (null != checkBoard) {
            createMessage(lithiumUtils, checkBoard, accToken);
        } else {
            String boardId = createBoard(lithiumUtils, accToken);
            createMessage(lithiumUtils, boardId, accToken);
        }
    }

    private static String checkingBoard(LithiumUtils lithiumUtils, String accToken) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("./config/application.props"));
            String checkBoardUrl = prop.getProperty("CHECK_BOARD");
            String BOARD_ID = prop.getProperty("BOARD_ID");
            checkBoardUrl = checkBoardUrl.replace("%SESSION_KEY%", accToken);
            checkBoardUrl = checkBoardUrl.replace("%BOARD_ID%", BOARD_ID);
            HttpResponse createMsg = lithiumUtils.HttpPostRequest(checkBoardUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(createMsg));
            logger.info("checking board status :"+jsonNode);
            String board = jsonNode.get("response").get("value").get("$").asText();
            return board;
        } catch (Exception e) {
            /* don't care */
        }
        return null;
    }

    public static String generateSessionKey(String accTokenUrl, LithiumUtils lithiumUtils) throws Exception {
        HttpResponse response = lithiumUtils.httpGetRequest(accTokenUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(response));
        String sessionKey = jsonNode.get("response").get("value").get("$").asText();
        logger.info("session key : " + sessionKey);
        return sessionKey;

    }

    private static String createBoard(LithiumUtils lithiumUtils, String accToken) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        String createBoardUrl = prop.getProperty("CREATE_BOARD_URL");
        String BOARD_ID = prop.getProperty("BOARD_ID");
        createBoardUrl = createBoardUrl.replace("%SESSION_KEY%", accToken);
        createBoardUrl = createBoardUrl.replace("%BOARD_ID%", BOARD_ID);
        HttpResponse createBoardResult = lithiumUtils.HttpPostRequest(createBoardUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(createBoardResult));
        String boardId = jsonNode.get("response").get("board").get("id").get("$").asText();
        logger.info("created board Id : " + boardId);
        return boardId;
    }

    private static void createMessage(LithiumUtils lithiumUtils, String boardId, String accToken) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("./config/application.props"));
        String createMsgUrl = prop.getProperty("MSG_URL");
        createMsgUrl = createMsgUrl.replace("%SESSION_KEY%", accToken);
        createMsgUrl = createMsgUrl.replace("%BOARD_ID%", boardId);
        HttpResponse createMsg = lithiumUtils.HttpPostRequest(createMsgUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode msgJsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(createMsg));
        logger.info("message response : " + msgJsonNode);
        savingMsgInDataBase(msgJsonNode,accToken);
    }

    private static void savingMsgInDataBase(JsonNode msgJsonNode, String accToken) throws ParseException {
        String msgId = msgJsonNode.get("response").get("message").get("id").get("$").asText();
        String msgSubject = msgJsonNode.get("response").get("message").get("subject").get("$").asText();
        String msgHref = msgJsonNode.get("response").get("message").get("href").asText();
        String msgLastTime = msgJsonNode.get("response").get("message").get("last_edit_time").get("$").asText();
        String msgPostTime = msgJsonNode.get("response").get("message").get("post_time").get("$").asText();
        String msgAuthor = msgJsonNode.get("response").get("message").get("author").get("login").get("$").asText();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date lastTime = formatter.parse(msgLastTime);
        Date postTime = formatter.parse(msgPostTime);
        LithiumMessage msg = new LithiumMessage();
        msg.setMessage(msgSubject);
        msg.setMsgId(msgId);
        msg.setHref(msgHref);
        msg.setLastEditTime(lastTime);
        msg.setPostTime(postTime);
        msg.setAuthor(msgAuthor);
        String Id = LithiumMessageDAO.createRecord(msg);
        logger.info("message saved in database id : " + Id);
        createMsgReply(Id,accToken);
    }

    private static void createMsgReply(String id, String accToken) {
        try {
            LithiumUtils lithiumUtils = new LithiumUtils();
            Properties prop = new Properties();
            prop.load(new FileInputStream("./config/application.props"));
            String createReplyUrl = prop.getProperty("CREATE_MSG_REPLY");
            createReplyUrl = createReplyUrl.replace("%SESSION_KEY%", accToken);
            createReplyUrl = createReplyUrl.replace("%MSG_ID%", id);
            HttpResponse msgReply = lithiumUtils.HttpPostRequest(createReplyUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode replyJsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(msgReply));
            logger.info("message response : " + replyJsonNode);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
