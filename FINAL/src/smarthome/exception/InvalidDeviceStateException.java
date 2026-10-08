package smarthome.exception;

public class InvalidDeviceStateException extends RuntimeException {
//    private static final long serialVersionUID = 1L;

    public InvalidDeviceStateException(String message) {
        super(message);
    }
}
