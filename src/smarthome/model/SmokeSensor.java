package smarthome.model;

/**
 * Sensor reporting whether smoke is currently detected.
 */
public class SmokeSensor extends Sensor {

    private static final long serialVersionUID = 1L;

    private boolean smokeDetected;

    public SmokeSensor(String sensorName, String roomId) {
        super(sensorName, roomId);
        this.smokeDetected = false;
    }

    public boolean isSmokeDetected() {
        return smokeDetected;
    }

    public void setSmokeDetected(boolean smokeDetected) {
        this.smokeDetected = smokeDetected;
    }

    @Override
    public String getSensorType() {
        return "Smoke";
    }

    @Override
    public String getFormattedValue() {
        return smokeDetected ? "SMOKE DETECTED" : "Normal";
    }
}
