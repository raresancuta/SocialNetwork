package ubb.scs.map.socialnetwork.repository.memory;

import ubb.scs.map.socialnetwork.domain.Entity;
import ubb.scs.map.socialnetwork.repository.Repository;
import ubb.scs.map.socialnetwork.repository.RepositoryException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * InMemoryRepository is a generic repository implementation that stores entities in memory using a HashMap.
 * It provides basic CRUD operations (Create, Read, Update, Delete) for managing entities.
 *
 * @param <ID> the type of the entity's identifier
 * @param <E> the type of the entity, which extends Entity<ID>
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected Map<ID, E> entities;

    public InMemoryRepository() {
        entities = new HashMap<>();
    }


    @Override
    public Optional<E> findOne(ID id)  {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }


    @Override
    public Optional<E> save(E entity) {
        if(entities==null){
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if(entities.containsKey(entity.getId())){
            return Optional.of(entity);
        }
        else{
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
    }


    @Override
    public Optional<E> delete(E entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return Optional.ofNullable(entities.remove(entity.getId()));
}


    @Override
    public Optional<E> update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        Optional<E> ent = findOne(entity.getId());
        if (ent.isPresent()) {
            entities.put(ent.get().getId(), entity);
            return Optional.empty();
        }
        else return Optional.of(entity);
    }
}
