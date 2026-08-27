package smarthome.model;

import smarthome.interfaces.Controllable;
import smarthome.interfaces.Switchable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class for every smart device in the system.
 *
 * Demonstrates:
 *  - ABSTRACTION (abstract class, cannot be instantiated directly)
 *  - ENCAPSULATION (private fields with public getters/setters)
 *  - INHERITANCE (all concrete devices extend this class)
 *  - Implements Switchable and Controllable INTERFACES
 */
public abstract class Device implements Switchable, Controllable, Serializable {

    private static final long serialVersionUID = 1L;

    /** Shared counter used to generate friendly sequential IDs. */
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    private final String deviceId;
    private String deviceName;
    private String roomId;
    private boolean isOn;
    private final LocalDateTime createdTime;

    /**
     * Constructor used by all subclasses.
     * Demonstrates CONSTRUCTOR usage and encapsulated field initialisation.
     *
     * @param deviceName human readable name, must not be blank
     * @param roomId     ID of the room this device belongs to
     */
    protected Device(String deviceName, String roomId) {
        if (deviceName == null || deviceName.isBlank()) {
            throw new IllegalArgumentException("Device name cannot be empty");
        }
        this.deviceId = "DEV-" + ID_COUNTER.getAndIncrement();
        this.deviceName = deviceName.trim();
        this.roomId = roomId;
        this.isOn = false;
        this.createdTime = LocalDateTime.now();
    }

    // ---------- Switchable implementation (default behaviour) ----------

    /**
     * Default ON behaviour shared by all devices.
     * Subclasses may override to add extra behaviour but generally
     * just rely on this base implementation (POLYMORPHISM through
     * method overriding is used only where a subclass needs it,
     * e.g. SecurityCamera enabling monitoring).
     */
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

    /**
     * Every concrete device type must describe itself differently.
     * Demonstrates ABSTRACTION + POLYMORPHISM (overridden per subclass).
     */
    public abstract String getDeviceType();

    /**
     * Every concrete device type calculates power draw differently.
     * Declared here (Controllable interface) but left abstract so each
     * subclass supplies its own formula.
     */
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
        if (this == o) return true;
        if (!(o instanceof Device device)) return false;
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
