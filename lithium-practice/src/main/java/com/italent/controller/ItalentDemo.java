package com.italent.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

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
    static LithiumUtils lithiumUtils = null;
    public static final String GET_SESSION_KEY_URL = "authentication/sessions/login?user.login=saikumarn&user.password=Q!w2e3r4&restapi.response_format=json";
    public static final String CREATE_BOARD_URL = "categories/id/Learn_Java_Lithium/boards/add?board.id=%BOARD_ID%&board.title=java1_test&restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String MSG_URL = "boards/id/%BOARD_ID%/messages/post?message.subject=messagesengfromeclipse&restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String CHECK_BOARD = "boards/id/%BOARD_ID%/id?restapi.session_key=%SESSION_KEY%&restapi.response_format=json";
    public static final String CREATE_MSG_REPLY = "messages/id/%MSG_ID%/reply?restapi.session_key=%SESSION_KEY%&message.subject=replyoftheabovemessage&restapi.response_format=json";
    private static String DEMO_URL = null;
    private static ResourceBundle bundle = null;

    public ItalentDemo() throws FileNotFoundException, IOException {
        lithiumUtils = new LithiumUtils();
        ResourceBundle bundle = ResourceBundle.getBundle("application");
        DEMO_URL = bundle.getString("ITALENT_DEMO_URL");
    }

    public static void main(String[] args) {
        try {
            String accTokenUrl = DEMO_URL + GET_SESSION_KEY_URL;
            logger.info("Lithium session key api : " + accTokenUrl);
            String accToken = lithiumUtils.generateSessionKey(accTokenUrl);
            String checkBoard = checkingBoard(accToken);
            if (null != checkBoard) {
                createMessage(checkBoard, accToken);
            } else {
                String boardId = createBoard(accToken);
                createMessage(boardId, accToken);
            }
        } catch (Exception e) {
            logger.error("error in ItalentDemo Main method():" + e, e);
        }
    }

    private static String checkingBoard(String accToken) {
        try {
            String checkBoardUrl = DEMO_URL + CHECK_BOARD;
            String BOARD_ID = bundle.getString("BOARD_ID");
            checkBoardUrl = checkBoardUrl.replace("%SESSION_KEY%", accToken);
            checkBoardUrl = checkBoardUrl.replace("%BOARD_ID%", BOARD_ID);
            HttpResponse createMsg = lithiumUtils.HttpPostRequest(checkBoardUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(createMsg));
            logger.info("checking board status :" + jsonNode);
            String board = jsonNode.get("response").get("value").get("$").asText();
            return board;
        } catch (Exception e) {
            /* don't care */
        }
        return null;
    }

    private static String createBoard(String accToken) throws Exception {
        String createBoardUrl = DEMO_URL + CREATE_BOARD_URL;
        String BOARD_ID = bundle.getString("BOARD_ID");
        createBoardUrl = createBoardUrl.replace("%SESSION_KEY%", accToken);
        createBoardUrl = createBoardUrl.replace("%BOARD_ID%", BOARD_ID);
        HttpResponse createBoardResult = lithiumUtils.HttpPostRequest(createBoardUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(createBoardResult));
        String boardId = jsonNode.get("response").get("board").get("id").get("$").asText();
        logger.info("created board Id : " + boardId);
        return boardId;
    }

    private static void createMessage(String boardId, String accToken) throws Exception {
        String createMsgUrl = DEMO_URL + MSG_URL;
        createMsgUrl = createMsgUrl.replace("%SESSION_KEY%", accToken);
        createMsgUrl = createMsgUrl.replace("%BOARD_ID%", boardId);
        HttpResponse createMsg = lithiumUtils.HttpPostRequest(createMsgUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode msgJsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(createMsg));
        logger.info("message response : " + msgJsonNode);
        savingMsgInDataBase(msgJsonNode, accToken);
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
        createMsgReply(Id, accToken);
    }

    private static void createMsgReply(String id, String accToken) {
        try {
            String createReplyUrl = DEMO_URL + CREATE_MSG_REPLY;
            createReplyUrl = createReplyUrl.replace("%SESSION_KEY%", accToken);
            createReplyUrl = createReplyUrl.replace("%MSG_ID%", id);
            HttpResponse msgReply = lithiumUtils.HttpPostRequest(createReplyUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode replyJsonNode = objectMapper.readTree(new BasicResponseHandler().handleResponse(msgReply));
            logger.info("message response : " + replyJsonNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
