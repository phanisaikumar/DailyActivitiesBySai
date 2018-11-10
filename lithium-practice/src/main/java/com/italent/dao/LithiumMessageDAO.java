package com.italent.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.italent.model.LithiumMessage;
import com.italent.model.Roles;
import com.italent.model.UserRoles;
import com.italent.model.Users;

public class LithiumMessageDAO {
    public static final Logger logger = LogManager.getLogger(LithiumMessageDAO.class.getName());
    public static Session sessionObj = null;

    public static SessionFactory getSessionFactory() {
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");
        ServiceRegistry serviceRegObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties())
                .build();
        SessionFactory factoryObj = configObj.buildSessionFactory(serviceRegObj);
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
        }
        return null;
    }

    public static int saveUsers(Users user) {
        try {
            Session sessionObj = getSessionFactory().openSession();
            Transaction transObj = sessionObj.beginTransaction();
            sessionObj.saveOrUpdate(user);
            transObj.commit();
            if (sessionObj.isOpen()) {
                sessionObj.close();
            }
            return user.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (sessionObj.isOpen()) {
                    sessionObj.close();
                }
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static int saveRoles(Roles roles) {
        Session sessionObj = null;
        try {
            sessionObj = getSessionFactory().openSession();
            Transaction transObj = sessionObj.beginTransaction();
            sessionObj.saveOrUpdate(roles);
            transObj.commit();
            sessionObj.close();
            return roles.getRoleId();
        } catch (Exception e) {
            logger.info("saveRoles():" + e, e);
        }
        return 0;
    }

    public static int saveUserRoles(UserRoles userRoles) {
        try {
            Session sessionObj = getSessionFactory().openSession();
            Transaction transObj = sessionObj.beginTransaction();
            sessionObj.save(userRoles);
            transObj.commit();
            sessionObj.close();
            return userRoles.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
