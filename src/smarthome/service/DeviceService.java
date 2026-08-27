package smarthome.service;

import smarthome.exception.DeviceNotFoundException;
import smarthome.exception.RoomNotFoundException;
import smarthome.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for device management.
 *
 * FACTORY PATTERN: createDevice() centralises the logic of choosing
 * which concrete Device subclass to instantiate based on a type
 * string, so callers (the UI) never need to know about the concrete
 * classes directly - they just pass a type name.
 */
public class DeviceService {

    private final Home home;

    public DeviceService(Home home) {
        this.home = home;
    }

    /** The set of device type names the factory understands, used to populate UI combo boxes. */
    public static final String[] DEVICE_TYPES = {
            "Light", "Fan", "Air Conditioner", "Security Camera", "Smart Lock", "Smart TV"
    };

    /**
     * FACTORY METHOD: creates the correct concrete Device subclass
     * based on the given type name, registers it with the home and
     * with its room, and returns it.
     *
     * @param deviceType one of DEVICE_TYPES
     * @param deviceName human-readable name
     * @param roomId     room to attach the device to
     * @return the newly created device
     * @throws RoomNotFoundException if roomId does not exist
     */
    public Device createDevice(String deviceType, String deviceName, String roomId)
            throws RoomNotFoundException {
        Room room = home.getRooms().get(roomId);
        if (room == null) {
            throw new RoomNotFoundException(roomId);
        }

        Device device = switch (deviceType) {
            case "Light" -> new Light(deviceName, roomId);
            case "Fan" -> new Fan(deviceName, roomId);
            case "Air Conditioner" -> new AirConditioner(deviceName, roomId);
            case "Security Camera" -> new SecurityCamera(deviceName, roomId);
            case "Smart Lock" -> new SmartLock(deviceName, roomId);
            case "Smart TV" -> new SmartTV(deviceName, roomId);
            default -> throw new IllegalArgumentException("Unknown device type: " + deviceType);
        };

        home.getDevices().put(device.getDeviceId(), device);
        room.addDeviceId(device.getDeviceId());
        home.addLog(SystemLog.EventType.DEVICE, device.getDeviceName() + " added to " + room.getRoomName());
        return device;
    }

    public void removeDevice(String deviceId) throws DeviceNotFoundException {
        Device device = getDeviceOrThrow(deviceId);
        home.getDevices().remove(deviceId);
        Room room = home.getRooms().get(device.getRoomId());
        if (room != null) {
            room.removeDeviceId(deviceId);
        }
        home.addLog(SystemLog.EventType.DEVICE, device.getDeviceName() + " removed");
    }

    public void turnOnDevice(String deviceId) throws DeviceNotFoundException {
        Device device = getDeviceOrThrow(deviceId);
        device.turnOn();
        home.addLog(SystemLog.EventType.DEVICE, device.getDeviceName() + " turned ON");
    }

    public void turnOffDevice(String deviceId) throws DeviceNotFoundException {
        Device device = getDeviceOrThrow(deviceId);
        device.turnOff();
        home.addLog(SystemLog.EventType.DEVICE, device.getDeviceName() + " turned OFF");
    }

    public Device getDevice(String deviceId) throws DeviceNotFoundException {
        return getDeviceOrThrow(deviceId);
    }

    public List<Device> getAllDevices() {
        return new ArrayList<>(home.getDevices().values());
    }

    public List<Device> getDevicesInRoom(String roomId) {
        List<Device> result = new ArrayList<>();
        for (Device d : home.getDevices().values()) {
            if (roomId != null && roomId.equals(d.getRoomId())) {
                result.add(d);
            }
        }
        return result;
    }

    public int getActiveDeviceCount() {
        int count = 0;
        for (Device d : home.getDevices().values()) {
            if (d.isOn()) count++;
        }
        return count;
    }

    public double getTotalPowerConsumption() {
        double total = 0.0;
        for (Device d : home.getDevices().values()) {
            total += d.getCurrentPowerConsumption();
        }
        return total;
    }

    private Device getDeviceOrThrow(String deviceId) throws DeviceNotFoundException {
        Device device = home.getDevices().get(deviceId);
        if (device == null) {
            throw new DeviceNotFoundException(deviceId);
        }
        return device;
    }
}
