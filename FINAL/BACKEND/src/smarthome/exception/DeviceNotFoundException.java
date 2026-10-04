package smarthome.exception;

public class DeviceNotFoundException extends Exception {

//    private static final long serialVersionUID = 1L;
    public DeviceNotFoundException(String deviceId) {
        super("Device not found: " + deviceId);
    }
}
