package ubb.scs.map.socialnetwork.repository.file;

import ubb.scs.map.socialnetwork.domain.User;
/**
 * UserFileRepository is a concrete implementation of the
 * AbstractFileRepository class for managing User entities.
 * It provides functionality to create User entities from
 * file lines and to convert User entities into a string
 * representation for saving to a file.
 */
public class UserFileRepository extends AbstractFileRepository<Long, User> {

    public UserFileRepository(String filename) {
        super(filename);
    }

    /**
     * Creates a User entity from a line of text.
     * The line should contain user data separated by semicolons
     * in the following format: id;firstName;lastName.
     *
     * @param line the line of text representing the user
     * @return the created User entity
     */
    @Override
    public User createEntity(String line) {
        String[] splited = line.split(";");
        User u = new User(splited[1], splited[2],splited[3]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    /**
     * Converts a User entity to a string representation for saving to a file.
     * The resulting string format is: id;firstName;lastName.
     *
     * @param entity the User entity to be converted to a string
     * @return the string representation of the User entity
     */
    @Override
    public String saveEntity(User entity) {
        String s = entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getEmail();
        return s;
    }
}
