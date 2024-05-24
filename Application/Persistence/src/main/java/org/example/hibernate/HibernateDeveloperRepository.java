package org.example.hibernate;

import org.example.Admin;
import org.example.Developer;
import org.example.interfaces.DeveloperRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Objects;

public class HibernateDeveloperRepository implements DeveloperRepository {
    @Override
    public Developer findDeveloperByUsernameAndPassword(String username, String password) {
        //closeSession();
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            var result =  session.createSelectionQuery("from Developer where username=:usernameM and password=:passwordM", Developer.class)
                    .setParameter("usernameM", username)
                    .setParameter("passwordM", password);
            transaction.commit();
            return result.getSingleResultOrNull();
        }
    }

    @Override
    public Developer save(Developer developer) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(developer));
        return developer;
    }

    @Override
    public Developer delete(Integer id) {
        Developer deletedDeveloper = findById(id);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Developer developer=session.createQuery("from Developer where id=?",Developer.class).
                    setParameter(1,id).uniqueResult();
            System.out.println("Developer with the username "+developer.getUsername()+" was successfully deleted!");
            if (developer != null) {
                session.remove(developer);
                session.flush();
            }
        });
        return deletedDeveloper;
    }

    @Override
    public Developer update(Developer developer) {
        Developer oldDeveloper = findById(developer.getId());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Developer.class, developer.getId()))) {
                System.out.println("Developer with the id "+developer.getId()+" was successfully updated!");
                session.merge(developer);
                session.flush();
            }
        });
        return oldDeveloper;
    }

    @Override
    public Developer findById(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Developer where id=:idM ", Developer.class)
                    .setParameter("idM", id)
                    .getSingleResultOrNull();
        }
    }

    @Override
    public Iterable<Developer> findAll() {
        try( Session session=HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Developer ", Developer.class).getResultList();
        }
    }

    @Override
    public void closeSession() {
        HibernateUtils.closeSessionFactory();
    }
}
