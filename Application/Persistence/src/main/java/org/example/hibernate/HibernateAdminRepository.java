package org.example.hibernate;

import org.example.Admin;
import org.example.interfaces.AdminRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Objects;

public class HibernateAdminRepository implements AdminRepository {

    @Override
    public Admin save(Admin admin) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(admin));
        return admin;
    }

    @Override
    public Admin delete(Integer id) {
        Admin deletedAdmin = findById(id);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Admin admin=session.createQuery("from Admin where id=?",Admin.class).
                    setParameter(1,id).uniqueResult();
            System.out.println("Admin with the username "+admin.getUsername()+" was successfully deleted!");
            if (admin != null) {
                session.remove(admin);
                session.flush();
            }
        });
        return deletedAdmin;
    }


    @Override
    public Admin update(Admin admin) {
        Admin oldAdmin = findById(admin.getId());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Admin.class, admin.getId()))) {
                System.out.println("Admin with the id "+admin.getId()+" was successfully updated!");
                session.merge(admin);
                session.flush();
            }
        });
        return oldAdmin;
    }

    @Override
    public Admin findById(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Admin where id=:idM ", Admin.class)
                    .setParameter("idM", id)
                    .getSingleResultOrNull();
        }
    }

    @Override
    public Iterable<Admin> findAll() {
        try( Session session=HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Admin ", Admin.class).getResultList();
        }
    }

    @Override
    public void closeSession() {
        HibernateUtils.closeSessionFactory();
    }

    @Override
    public Admin findAdminByUsernameAndPassword(String username, String password) {
        //closeSession();
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            var result = session.createSelectionQuery("from Admin where username=:usernameM and password=:passwordM", Admin.class)
                    .setParameter("usernameM", username)
                    .setParameter("passwordM", password);
            transaction.commit();
            return result.getSingleResultOrNull();
        }

    }
}
