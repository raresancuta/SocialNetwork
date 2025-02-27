package ubb.scs.map.socialnetwork.domain.validators;

/**
 * The interface of Validator
 * @param <T>
 */
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
