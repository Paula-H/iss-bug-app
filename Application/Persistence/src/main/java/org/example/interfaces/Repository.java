package org.example.interfaces;

public interface Repository<Entity> {
    Entity save(Entity entity);
    Entity delete(Integer id);
    Entity update(Entity entity);
    Entity findById(Integer id);
    Iterable<Entity> findAll();
    void closeSession();
}
