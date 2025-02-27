package ubb.scs.map.socialnetwork.domain.validators;

/**
 * The ValidationException class is a custom exception that is thrown
 * to indicate a validation error. It extends the RuntimeException,
 * allowing it to be used in scenarios where a checked exception
 * is not appropriate.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(){

    }
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
      super(message, cause);
    }
    public ValidationException(Throwable cause) {
      super(cause);
    }
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
}
