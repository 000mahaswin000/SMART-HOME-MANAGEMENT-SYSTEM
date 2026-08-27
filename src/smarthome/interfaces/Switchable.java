package smarthome.interfaces;

/**
 * Contract for any device that can be switched on and off.
 * Demonstrates INTERFACE and ABSTRACTION.
 */
public interface Switchable {

    /** Turn the device on. */
    void turnOn();

    /** Turn the device off. */
    void turnOff();

    /** @return true if the device is currently on. */
    boolean isOn();
}
