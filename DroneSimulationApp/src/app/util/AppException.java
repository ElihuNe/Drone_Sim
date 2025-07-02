package app.util;

/**
 * Custom runtime exception for application-specific errors.
 * Used to wrap lower-level exceptions with meaningful context.
 */
public class AppException extends RuntimeException {

    /**
     * Creates a new AppException with a message.
     *
     * @param message the error message to describe the cause
     */
    public AppException(String message) {
        super(message);
    }
}