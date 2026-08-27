package smarthome.model;

/**
 * Sensor reporting whether a door is open or closed.
 */
public class DoorSensor extends Sensor {

    private static final long serialVersionUID = 1L;

    private boolean open;

    public DoorSensor(String sensorName, String roomId) {
        super(sensorName, roomId);
        this.open = false; // closed by default
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String getSensorType() {
        return "Door";
    }

    @Override
    public String getFormattedValue() {
        return open ? "OPEN" : "Closed";
    }
}
