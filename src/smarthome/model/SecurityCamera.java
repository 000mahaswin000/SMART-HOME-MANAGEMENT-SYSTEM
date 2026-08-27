package smarthome.model;

/**
 * Concrete device: SecurityCamera.
 * Tracks monitoring status and whether motion is currently detected.
 * Demonstrates METHOD OVERRIDING of turnOn()/turnOff() to add extra
 * behaviour beyond the base Device implementation (POLYMORPHISM).
 */
public class SecurityCamera extends Device {

    private static final long serialVersionUID = 1L;
    private static final double WATTS_MONITORING = 8.0;

    private boolean monitoring;
    private boolean motionDetected;

    public SecurityCamera(String deviceName, String roomId) {
        super(deviceName, roomId);
        this.monitoring = false;
        this.motionDetected = false;
    }

    /**
     * Overridden so that turning the camera on also starts monitoring.
     * Demonstrates METHOD OVERRIDING - extends parent behaviour with super call.
     */
    @Override
    public void turnOn() {
        super.turnOn();
        this.monitoring = true;
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.monitoring = false;
        this.motionDetected = false;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    /** Called by the sensor simulation / automation engine when motion occurs. */
    public void setMotionDetected(boolean motionDetected) {
        this.motionDetected = motionDetected;
    }

    @Override
    public String getDeviceType() {
        return "SecurityCamera";
    }

    @Override
    public double getCurrentPowerConsumption() {
        return isOn() ? WATTS_MONITORING : 0.0;
    }

    @Override
    public String getStatusSummary() {
        if (!isOn()) return "OFF";
        return "ON, Monitoring" + (motionDetected ? ", MOTION DETECTED" : ", Clear");
    }
}
