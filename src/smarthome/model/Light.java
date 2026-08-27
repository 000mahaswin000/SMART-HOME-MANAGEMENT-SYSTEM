package smarthome.model;

import smarthome.exception.InvalidDeviceStateException;

/**
 * Concrete device: Light.
 * Demonstrates INHERITANCE (extends Device) and METHOD OVERRIDING.
 */
public class Light extends Device {

    private static final long serialVersionUID = 1L;
    private static final double WATTS_AT_FULL_BRIGHTNESS = 10.0;

    private int brightness; // 0-100

    public Light(String deviceName, String roomId) {
        super(deviceName, roomId);
        this.brightness = 100; // default brightness when purchased
    }

    /** Overloaded constructor allowing an initial brightness to be supplied.
     *  Demonstrates CONSTRUCTOR OVERLOADING. */
    public Light(String deviceName, String roomId, int initialBrightness) {
        super(deviceName, roomId);
        setBrightness(initialBrightness);
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new InvalidDeviceStateException(
                    "Brightness must be between 0 and 100, got: " + brightness);
        }
        this.brightness = brightness;
    }

    @Override
    public String getDeviceType() {
        return "Light";
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn()) return 0.0;
        return WATTS_AT_FULL_BRIGHTNESS * (brightness / 100.0);
    }

    @Override
    public String getStatusSummary() {
        return isOn() ? ("ON, Brightness: " + brightness + "%") : "OFF";
    }
}
