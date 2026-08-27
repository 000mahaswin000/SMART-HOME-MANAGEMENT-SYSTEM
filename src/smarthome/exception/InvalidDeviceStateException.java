package smarthome.exception;

/**
 * Thrown when a device is asked to enter an invalid state,
 * e.g. AC temperature outside 16-30, brightness outside 0-100.
 * This is an unchecked exception because it represents a
 * programming/validation error that should surface immediately.
 */
public class InvalidDeviceStateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidDeviceStateException(String message) {
        super(message);
    }
}
