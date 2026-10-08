package smarthome.model;

/**
 * Sensor reporting whether motion is currently detected.
 */
public class MotionSensor extends Sensor {

    private static final long serialVersionUID = 1L;

    private boolean motionDetected;

    public MotionSensor(String sensorName, String roomId) {
        super(sensorName, roomId);
        this.motionDetected = false;
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    public void setMotionDetected(boolean motionDetected) {
        this.motionDetected = motionDetected;
    }

    @Override
    public String getSensorType() {
        return "Motion";
    }

    @Override
    public String getFormattedValue() {
        return motionDetected ? "MOTION DETECTED" : "Normal";
    }
}
