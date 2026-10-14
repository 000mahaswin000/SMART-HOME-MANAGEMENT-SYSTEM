package smarthome.exception;

public class DeviceNotFoundException extends Exception {

    public DeviceNotFoundException(String deviceId) {
        super("Device not found: " + deviceId);
    }
}
