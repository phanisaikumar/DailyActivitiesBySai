package com.italent.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.italent.model.LithiumMessage;
import com.italent.model.Users;

public class LithiumMessageDAO {

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
            sessionObj.close();
            return user.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
