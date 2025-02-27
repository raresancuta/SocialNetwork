package ubb.scs.map.socialnetwork.domain;

import java.util.Objects;

/**
 * User is a class that represents a user in the system, extending the {@link Entity} class with a {@link Long} ID.
 * Each user has a first name and a last name.
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructs a new User with the specified first name and last name.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public User(String firstName, String lastName,String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(User u){
        this.setId(u.getId());
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.email = u.getEmail();
    }
    /**
     * Gets the user's first name.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the new first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the new last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns a string representation of the user, including the ID, first name, and last name.
     *
     * @return a string representation of the user
     */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    /**
     * Compares this user with the specified object for equality.
     * Two users are considered equal if they have the same first name and last name.
     *
     * @param o the object to be compared
     * @return {@code true} if the specified object is equal to this user, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) && getLastName().equals(that.getLastName()) && getEmail().equals(that.getEmail());
    }

    /**
     * Returns the hash code value for this user.
     * The hash code is based on the user's first name and last name.
     *
     * @return the hash code value for this user
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getEmail());
    }
}
