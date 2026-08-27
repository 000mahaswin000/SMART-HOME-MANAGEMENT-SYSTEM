package smarthome.model;

import smarthome.exception.InvalidDeviceStateException;

/**
 * Concrete device: AirConditioner.
 * Temperature range 16-30 C, mode COOL or FAN.
 */
public class AirConditioner extends Device {

    private static final long serialVersionUID = 1L;
    public static final int MIN_TEMP = 16;
    public static final int MAX_TEMP = 30;
    private static final double WATTS_COOL_MODE = 1500.0;
    private static final double WATTS_FAN_MODE = 75.0;

    /** Nested enum - keeps AC modes type-safe. */
    public enum Mode {
        COOL, FAN
    }

    private int temperature;
    private Mode mode;

    public AirConditioner(String deviceName, String roomId) {
        super(deviceName, roomId);
        this.temperature = 24;
        this.mode = Mode.COOL;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        if (temperature < MIN_TEMP || temperature > MAX_TEMP) {
            throw new InvalidDeviceStateException(
                    "AC temperature must be between " + MIN_TEMP + " and " + MAX_TEMP
                            + ", got: " + temperature);
        }
        this.temperature = temperature;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        if (mode == null) {
            throw new InvalidDeviceStateException("AC mode cannot be null");
        }
        this.mode = mode;
    }

    @Override
    public String getDeviceType() {
        return "AirConditioner";
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn()) return 0.0;
        return mode == Mode.COOL ? WATTS_COOL_MODE : WATTS_FAN_MODE;
    }

    @Override
    public String getStatusSummary() {
        return isOn()
                ? ("ON, " + temperature + "\u00B0C, Mode: " + mode)
                : "OFF";
    }
}
