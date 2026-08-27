package smarthome.model;

import smarthome.exception.InvalidDeviceStateException;

/**
 * Concrete device: Fan. Speed levels 0-5.
 */
public class Fan extends Device {

    private static final long serialVersionUID = 1L;
    private static final double WATTS_PER_SPEED_LEVEL = 12.0;

    private int speedLevel; // 0-5

    public Fan(String deviceName, String roomId) {
        super(deviceName, roomId);
        this.speedLevel = 3; // default medium speed
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public void setSpeedLevel(int speedLevel) {
        if (speedLevel < 0 || speedLevel > 5) {
            throw new InvalidDeviceStateException(
                    "Fan speed must be between 0 and 5, got: " + speedLevel);
        }
        this.speedLevel = speedLevel;
    }

    @Override
    public String getDeviceType() {
        return "Fan";
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn()) return 0.0;
        return WATTS_PER_SPEED_LEVEL * speedLevel;
    }

    @Override
    public String getStatusSummary() {
        return isOn() ? ("ON, Speed: " + speedLevel + "/5") : "OFF";
    }
}
