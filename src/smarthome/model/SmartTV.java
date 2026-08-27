package smarthome.model;

import smarthome.exception.InvalidDeviceStateException;

/**
 * Concrete device: SmartTV. Volume 0-100, channel 1-999.
 */
public class SmartTV extends Device {

    private static final long serialVersionUID = 1L;
    private static final double WATTS_ON = 120.0;

    private int volume;
    private int channel;

    public SmartTV(String deviceName, String roomId) {
        super(deviceName, roomId);
        this.volume = 20;
        this.channel = 1;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new InvalidDeviceStateException(
                    "Volume must be between 0 and 100, got: " + volume);
        }
        this.volume = volume;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        if (channel < 1 || channel > 999) {
            throw new InvalidDeviceStateException(
                    "Channel must be between 1 and 999, got: " + channel);
        }
        this.channel = channel;
    }

    @Override
    public String getDeviceType() {
        return "SmartTV";
    }

    @Override
    public double getCurrentPowerConsumption() {
        return isOn() ? WATTS_ON : 0.0;
    }

    @Override
    public String getStatusSummary() {
        return isOn()
                ? ("ON, Channel: " + channel + ", Volume: " + volume + "%")
                : "OFF";
    }
}
