package smarthome.exception;

/**
 * Thrown when a schedule is created with invalid parameters
 * (e.g. missing device, missing time, missing action).
 */
public class InvalidScheduleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidScheduleException(String message) {
        super(message);
    }
}
