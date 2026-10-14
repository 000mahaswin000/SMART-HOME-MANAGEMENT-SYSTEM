package smarthome.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import smarthome.interfaces.Controllable;
import smarthome.interfaces.Switchable;

//Abstract base class for every smart device in the system
public abstract class Device implements Switchable, Controllable, Serializable {

    private static int idCounter = 1;

    private final String deviceId;
    private String deviceName;
    private String roomId;
    private boolean isOn;
    private final LocalDateTime createdTime;

    protected Device(String deviceName, String roomId) {
        if (deviceName == null || deviceName.isBlank()) {
            throw new IllegalArgumentException("Device name cannot be empty");
        }

        this.deviceId = "DEV-" + idCounter++;
        this.deviceName = deviceName.trim();
        this.roomId = roomId;
        this.isOn = false;
        this.createdTime = LocalDateTime.now();
    }

    // ---------- Switchable implementation ----------
    @Override
    public void turnOn() {
        this.isOn = true;
    }

    @Override
    public void turnOff() {
        this.isOn = false;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    // ---------- Abstract methods each subclass MUST implement ----------
    public abstract String getDeviceType();

    @Override
    public abstract double getCurrentPowerConsumption();

    @Override
    public abstract String getStatusSummary();

    // ---------- Encapsulated getters / setters ----------
    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        if (deviceName == null || deviceName.isBlank()) {
            throw new IllegalArgumentException("Device name cannot be empty");
        }
        this.deviceName = deviceName.trim();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return getDeviceType() + " [" + deviceId + "] " + deviceName + " - " + getStatusSummary();
    }
}
