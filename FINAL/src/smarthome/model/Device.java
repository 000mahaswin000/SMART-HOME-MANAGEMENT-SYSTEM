package smarthome.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import smarthome.interfaces.Controllable;
import smarthome.interfaces.Switchable;

//Abstract base class for every smart device in the system
public abstract class Device implements Switchable, Controllable, Serializable {

    private static final long serialVersionUID = 4711797079774559737L;
    /**
     * Shared counter used to generate friendly sequential IDs.
     */
    private static int ID_COUNTER = 1;

    /** Advances the next ID beyond an ID restored from saved data. */
    public static synchronized void updateCounterFromId(String deviceId) {
        if (deviceId == null || !deviceId.startsWith("DEV-")) {
            return;
        }
        try {
            int id = Integer.parseInt(deviceId.substring(4));
            if (id >= ID_COUNTER) {
                ID_COUNTER = id + 1;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs and leave the counter unchanged.
        }
    }

    private static synchronized String nextId() {
        return "DEV-" + ID_COUNTER++;
    }

    private final String deviceId;
    private String deviceName;
    private String roomId;
    private boolean isOn;
    private final LocalDateTime createdTime;

    protected Device(String deviceName, String roomId) {
        if (deviceName == null || deviceName.isBlank()) {
            throw new IllegalArgumentException("Device name cannot be empty");
        }

        this.deviceId = nextId();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Device device)) {
            return false;
        }
        return deviceId.equals(device.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId);
    }

    @Override
    public String toString() {
        return getDeviceType() + " [" + deviceId + "] " + deviceName + " - " + getStatusSummary();
    }
}
