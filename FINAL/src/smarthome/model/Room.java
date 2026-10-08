package smarthome.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {

    //private static final long serialVersionUID = 1L;
    private static int ID_COUNTER = 1;

    private final String roomId;
    private String roomName;
    private final List<String> deviceIds;
    private final List<String> sensorIds;

    public Room(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        this.roomId = "ROOM-" + ID_COUNTER++;
        this.roomName = roomName.trim();
        this.deviceIds = new ArrayList<>();
        this.sensorIds = new ArrayList<>();
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

    @Override
    public String toString() {
        return roomName;
    }
}
