package smarthome.exception;

/**
 * Thrown when a device lookup by ID fails.
 * Demonstrates custom CHECKED EXCEPTION handling.
 */
public class DeviceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public DeviceNotFoundException(String deviceId) {
        super("Device not found: " + deviceId);
    }
}
