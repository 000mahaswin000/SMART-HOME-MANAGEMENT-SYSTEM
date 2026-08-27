package smarthome.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class for all sensors.
 * Demonstrates ABSTRACTION + ENCAPSULATION, mirrors the Device
 * hierarchy pattern for consistency.
 */
public abstract class Sensor implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    private final String sensorId;
    private String sensorName;
    private String roomId;
    private boolean active;

    protected Sensor(String sensorName, String roomId) {
        if (sensorName == null || sensorName.isBlank()) {
            throw new IllegalArgumentException("Sensor name cannot be empty");
        }
        this.sensorId = "SEN-" + ID_COUNTER.getAndIncrement();
        this.sensorName = sensorName.trim();
        this.roomId = roomId;
        this.active = true;
    }

    /** Every sensor type describes its own type string. */
    public abstract String getSensorType();

    /** Every sensor type formats its current value differently. */
    public abstract String getFormattedValue();

    public String getSensorId() {
        return sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        if (sensorName == null || sensorName.isBlank()) {
            throw new IllegalArgumentException("Sensor name cannot be empty");
        }
        this.sensorName = sensorName.trim();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getSensorType() + " [" + sensorId + "] " + sensorName + " = " + getFormattedValue();
    }
}
