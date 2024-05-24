package org.example.hibernate;

import org.example.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){
        if ((sessionFactory==null)||(sessionFactory.isClosed()))
            sessionFactory=createNewSessionFactory();
        return sessionFactory;
    }

    private static  SessionFactory createNewSessionFactory(){
        sessionFactory = new Configuration()
                .addAnnotatedClass(Admin.class)
                .addAnnotatedClass(Developer.class)
                .addAnnotatedClass(Tester.class)
                .addAnnotatedClass(Bug.class)
                .buildSessionFactory();
        return sessionFactory;
    }


    public static  void closeSessionFactory(){
        if (sessionFactory!=null)
            sessionFactory.close();
    }
}
