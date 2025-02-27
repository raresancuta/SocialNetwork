package ubb.scs.map.socialnetwork.repository.file;

import ubb.scs.map.socialnetwork.domain.Entity;
import ubb.scs.map.socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

/**
 * AbstractFileRepository is an abstract class that extends the InMemoryRepository
 * to provide file-based persistence for entities. It defines methods to load
 * entities from a file, save them to a file, and abstract methods for creating
 * and saving entity representations.
 *
 * @param <ID> the type of the entity ID
 * @param <E> the type of the entity that extends Entity<ID>
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename;

    public AbstractFileRepository(String filename) {
        this.filename = filename;
        loadData();
    }

    public abstract E createEntity(String line);


    public abstract String saveEntity(E entity);


    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (!e.isPresent()) {
            writeToFile();
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    /**
     * Writes all entities to the file.
     */
    private void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (E entity : entities.values()) {
                String ent = saveEntity(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads entities from the file and saves them to the in-memory repository.
     */
    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                E entity = createEntity(line);
                super.save(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing entity in the repository and writes to file.
     * If the entity does not exist, it will write all entities to the file.
     *
     * @param entity the entity to be updated
     * @return the updated entity or null if the entity does not exist
     */
    @Override
    public Optional<E> update(E entity) {
        Optional<E> e = super.update(entity);
        if (!e.isPresent()) {
            writeToFile();
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    /**
     * Deletes an entity from the repository and updates the file.
     *
     * @param entity who it's gonna be deleted
     * @return the deleted entity or null if it did not exist
     */
    @Override
    public Optional<E> delete(E entity) {
        Optional<E> optionalEntity = super.delete(entity);
        if (optionalEntity.isPresent()) {
            writeToFile();
            return Optional.of(entity);
        }
        return Optional.empty();
    }
}
