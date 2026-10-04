package smarthome.interfaces;

public interface Switchable {

    /**
     * Turn the device on.
     */
    void turnOn();

    /**
     * Turn the device off.
     */
    void turnOff();

    /**
     * @return true if the device is currently on.
     */
    boolean isOn();
}
