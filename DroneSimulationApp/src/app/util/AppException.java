package app.util;

import java.util.logging.Logger;

/**
 * Custom runtime exception for application-specific errors.
 */
public class AppException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(AppException.class.getName( ));

    /**
     * Creates a new AppException with a message.
     */
    public AppException(String message) {
        logger.warning(message);
    }
}