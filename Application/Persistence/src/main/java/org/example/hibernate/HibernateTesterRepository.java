package org.example.hibernate;

import org.example.Tester;
import org.example.interfaces.TesterRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Objects;

public class HibernateTesterRepository implements TesterRepository {
    @Override
    public Tester findTesterByUsernameAndPassword(String username, String password) {
        //closeSession();
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            var result =  session.createSelectionQuery("from Tester where username=:usernameM and password=:passwordM", Tester.class)
                    .setParameter("usernameM", username)
                    .setParameter("passwordM", password);
            transaction.commit();
            return result.getSingleResultOrNull();
        }
    }

    @Override
    public Tester save(Tester tester) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(tester));
        return tester;
    }

    @Override
    public Tester delete(Integer id) {
        Tester deletedTester = findById(id);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Tester tester=session.createQuery("from Tester where id=?",Tester.class).
                    setParameter(1,id).uniqueResult();
            System.out.println("Tester with the username "+tester.getUsername()+" was successfully deleted!");
            if (tester != null) {
                session.remove(tester);
                session.flush();
            }
        });
        return deletedTester;
    }

    @Override
    public Tester update(Tester tester) {
        Tester oldTester = findById(tester.getId());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Tester.class, tester.getId()))) {
                System.out.println("Tester with the id "+tester.getId()+" was successfully updated!");
                session.merge(tester);
                session.flush();
            }
        });
        return oldTester;
    }

    @Override
    public Tester findById(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Tester where id=:idM ", Tester.class)
                    .setParameter("idM", id)
                    .getSingleResultOrNull();
        }
    }

    @Override
    public Iterable<Tester> findAll() {
        try( Session session=HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Tester ", Tester.class).getResultList();
        }
    }

    @Override
    public void closeSession() {
        HibernateUtils.closeSessionFactory();
    }
}
