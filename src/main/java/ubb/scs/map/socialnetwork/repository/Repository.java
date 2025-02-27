package ubb.scs.map.socialnetwork.repository;

import ubb.scs.map.socialnetwork.domain.Entity;

import java.util.Optional;


public interface Repository<ID,E extends Entity<ID>> {
    /**
     * Finds and returns the entity associated with the specified ID.
     *
     * @param id the ID of the entity to be found
     * @return the entity associated with the specified ID
     * @throws RepositoryException if no entity exists with the specified ID
     */
    Optional<E> findOne(ID id);

    /**
     * Returns all the entities in the repository.
     *
     * @return an Iterable containing all the entities
     */
    Iterable<E> findAll();

    /**
     * Saves the specified entity to the repository.
     *
     * @param entity the entity to be saved
     * @return the existing entity with the same ID if it already exists, or null if the entity was successfully saved
     * @throws IllegalArgumentException if the provided entity is null
     */
    Optional<E> save(E entity);

    /**
     * Deletes the entity with the specified ID from the repository.
     *
     * @param id the ID of the entity to be deleted
     * @return the deleted entity, or null if no entity with the specified ID exists
     * @throws IllegalArgumentException if the provided ID is null
     */
    Optional<E> delete(E entity);

    /**
     * Updates the specified entity in the repository.
     *
     * @param entity the entity to be updated
     * @return null if the update was successful, or the entity itself if it does not exist in the repository
     * @throws IllegalArgumentException if the provided entity is null
     */
    Optional<E> update(E entity);
}
