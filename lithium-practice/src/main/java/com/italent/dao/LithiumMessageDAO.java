package com.italent.dao;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.fasterxml.jackson.databind.JsonNode;
import com.italent.model.LithiumMessage;
import com.italent.model.Roles;
import com.italent.model.UserRoles;
import com.italent.model.Users;

public class LithiumMessageDAO {
    public static final Logger logger = LogManager.getLogger(LithiumMessageDAO.class.getName());
    public static Session sessionObj = null;

    static SessionFactory factoryObj = null;
    static ServiceRegistry serviceRegObj = null;

    public static SessionFactory getSessionFactory() {
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");
        serviceRegObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();
        factoryObj = configObj.buildSessionFactory(serviceRegObj);
        return factoryObj;
    }

    public static String createRecord(LithiumMessage msg) {
        try {
            Session sessionObj = getSessionFactory().openSession();
            Transaction transObj = sessionObj.beginTransaction();
            sessionObj.saveOrUpdate(msg);
            transObj.commit();
            sessionObj.close();
            getSessionFactory().close();
            return msg.getMsgId();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sessionObj != null) {
                sessionObj.close();
            }
        }
        return null;
    }

    public static ArrayList<Integer> savingUsersDataInDataBase(JsonNode usersArray) {
        Transaction transObj = null;
        try {
            Session sessionObj = getSessionFactory().openSession();

            ArrayList<Integer> userList = new ArrayList<Integer>();
            for (JsonNode usersObj : usersArray) {
                transObj = sessionObj.beginTransaction();
                Users users = new Users();
                users.setUserId(usersObj.get("id").get("$").asInt());
                users.setUserName(usersObj.get("login").get("$").asText());
                users.setUserEmail(usersObj.get("email").get("$").asText());
                sessionObj.saveOrUpdate(users);
                logger.info("successfully saved user id :" + users.getUserId());
                userList.add(users.getUserId());
                transObj.commit();
            }

            sessionObj.close();
            return userList;
        } catch (Exception e) {
            logger.info("savingUserDataInDataBase():" + e, e);
        } finally {
            if (sessionObj != null) {
                sessionObj.close();
            }
        }
        return null;
    }

    public static ArrayList<Integer> savingRolesDataInDataBase(JsonNode rolesArray) {
        if (rolesArray.size() != 0) {
            Transaction transObj = null;
            try {
                // UserRoles userRoles = new UserRoles();
                Session sessionObj = getSessionFactory().openSession();
                ArrayList<Integer> rolesList = new ArrayList<Integer>();
                for (JsonNode rolesObj : rolesArray) {
                    transObj = sessionObj.beginTransaction();
                    Roles roles = new Roles();
                    roles.setRoleId(rolesObj.get("id").get("$").asInt());
                    roles.setRoleName(rolesObj.get("name").get("$").asText());
                    // userRoles.setUserId(list);
                    // userRoles.setRoleId(rolesObj.get("id").get("$").asInt());
                    // logger.info("userRoles obj :" + userRoles);
                    // LithiumMessageDAO.saveUserRoles(userRoles);
                    sessionObj.saveOrUpdate(roles);
                    transObj.commit();
                    logger.info("successfully saved role id :" + roles.getRoleId());
                    rolesList.add(roles.getRoleId());
                }
                return rolesList;

            } catch (Exception e) {
                logger.info("saveRoles():" + e, e);
            } finally {
                if (sessionObj != null) {
                    sessionObj.close();
                }
            }

        }
        return null;
    }

    public static void savingUsersRolesData(JsonNode rolesArray, int roleId) {
        try {
            Session sessionObj = getSessionFactory().openSession();
            Transaction transObj = null;
            for (JsonNode rolesObj : rolesArray) {
                transObj = sessionObj.beginTransaction();
                UserRoles userRoles = new UserRoles();
                int userId = rolesObj.get("id").get("$").asInt();
                String userName = rolesObj.get("login").get("$").asText();
                userRoles.setRoleId(roleId);
                userRoles.setUserId(userId);
                userRoles.setUserName(userName);
                sessionObj.save(userRoles);
                transObj.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sessionObj != null) {
                sessionObj.close();
            }
        }
    }

}
