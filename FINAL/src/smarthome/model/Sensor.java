package smarthome.model;

import java.io.Serializable;

/**
 * Abstract base class for all sensors.
 */
public abstract class Sensor implements Serializable {

    private static final long serialVersionUID = -2645952759138334034L;
    private static int ID_COUNTER = 1;

    /** Advances the next ID beyond an ID restored from saved data. */
    public static synchronized void updateCounterFromId(String sensorId) {
        if (sensorId == null || !sensorId.startsWith("SEN-")) {
            return;
        }
        try {
            int id = Integer.parseInt(sensorId.substring(4));
            if (id >= ID_COUNTER) {
                ID_COUNTER = id + 1;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs and leave the counter unchanged.
        }
    }

    private static synchronized String nextId() {
        return "SEN-" + ID_COUNTER++;
    }

    private final String sensorId;
    private String sensorName;
    private String roomId;
    private boolean active;

    protected Sensor(String sensorName, String roomId) {
        if (sensorName == null || sensorName.isBlank()) {
            throw new IllegalArgumentException("Sensor name cannot be empty");
        }
        this.sensorId = nextId();
        this.sensorName = sensorName.trim();
        this.roomId = roomId;
        this.active = true;
    }

    /**
     * Every sensor type describes its own type string.
     */
    public abstract String getSensorType();

    /**
     * Every sensor type formats its current value differently.
     */
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
