package org.example.hibernate;

import org.example.Bug;
import org.example.interfaces.BugRepository;



public class HibernateBugRepository implements BugRepository {
    @Override
    public Bug findBugByName(String name) {
        try {
            return HibernateUtils.getSessionFactory().openSession().createQuery("from Bug where name = :name", Bug.class).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Iterable<Bug> getUnfixedBugs() {
        try {
            return HibernateUtils.getSessionFactory().openSession().createQuery("from Bug where status = 0", Bug.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Bug save(Bug bug) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(bug));
        return bug;
    }

    @Override
    public Bug delete(Integer id) {
        Bug deletedBug = findById(id);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Bug bug = session.createQuery("from Bug where id = :id", Bug.class).setParameter("id", id).uniqueResult();
            if (bug != null) {
                session.remove(bug);
                session.flush();
            }
        });
        return deletedBug;
    }

    @Override
    public Bug update(Bug bug) {
        Bug oldBug = findById(bug.getId());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (session.find(Bug.class, bug.getId()) != null) {
                session.merge(bug);
                session.flush();
            }
        });
        return oldBug;
    }

    @Override
    public Bug findById(Integer id) {
        try {
            return HibernateUtils.getSessionFactory().openSession().createQuery("from Bug where id = :id", Bug.class).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Iterable<Bug> findAll() {
        try {
            return HibernateUtils.getSessionFactory().openSession().createQuery("from Bug", Bug.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void closeSession() {
        HibernateUtils.closeSessionFactory();
    }
}
