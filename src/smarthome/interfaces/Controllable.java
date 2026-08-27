package smarthome.interfaces;

/**
 * Contract for devices that expose a human-readable status string
 * and can report their current power consumption.
 * Demonstrates INTERFACE and POLYMORPHISM (each device implements
 * getStatusSummary() differently).
 */
public interface Controllable {

    /** @return a short human-readable summary of the device's current state. */
    String getStatusSummary();

    /** @return current power consumption in watts, based on device state. */
    double getCurrentPowerConsumption();
}
