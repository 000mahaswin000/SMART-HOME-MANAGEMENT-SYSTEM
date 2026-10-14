package smarthome.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room<D extends Device, S extends Sensor> implements Serializable {

    private static int idCounter = 1;

    private final String roomId;
    private String roomName;
    private final List<D> devices;   // Type-safe list of Devices
    private final List<S> sensors;   // Type-safe list of Sensors
    private final List<String> deviceIds;
    private final List<String> sensorIds;

    public Room(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        this.roomId = "ROOM-" + idCounter++;
        this.roomName = roomName.trim();
        this.devices = new ArrayList<>();
        this.sensors = new ArrayList<>();
        this.deviceIds = new ArrayList<>();
        this.sensorIds = new ArrayList<>();
    }

    // --- Device Operations ---
    public void addDevice(D device) {
        if (device != null && !devices.contains(device)) {
            devices.add(device);
            if (!deviceIds.contains(device.getDeviceId())) {
                deviceIds.add(device.getDeviceId());
            }
        }
    }

    public void removeDevice(D device) {
        if (device != null) {
            devices.remove(device);
            deviceIds.remove(device.getDeviceId());
        }
    }

    public List<D> getDevices() {
        return devices;
    }

    // --- Sensor Operations ---
    public void addSensor(S sensor) {
        if (sensor != null && !sensors.contains(sensor)) {
            sensors.add(sensor);
            if (!sensorIds.contains(sensor.getSensorId())) {
                sensorIds.add(sensor.getSensorId());
            }
        }
    }

    public void removeSensor(S sensor) {
        if (sensor != null) {
            sensors.remove(sensor);
            sensorIds.remove(sensor.getSensorId());
        }
    }

    public List<S> getSensors() {
        return sensors;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void addDeviceId(String deviceId) {
        if (!deviceIds.contains(deviceId)) {
            deviceIds.add(deviceId);
        }
    }

    public void removeDeviceId(String deviceId) {
        deviceIds.remove(deviceId);
    }

    public void addSensorId(String sensorId) {
        if (!sensorIds.contains(sensorId)) {
            sensorIds.add(sensorId);
        }
    }

    public void removeSensorId(String sensorId) {
        sensorIds.remove(sensorId);
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        this.roomName = roomName.trim();
    }

    @Override
    public String toString() {
        return roomName;
    }
}
