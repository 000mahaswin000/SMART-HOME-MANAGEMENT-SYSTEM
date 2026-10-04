package smarthome.interfaces;

public interface Controllable {

    /**
     * @return a short human-readable summary of the device's current state.
     */
    String getStatusSummary();

    /**
     * @return current power consumption in watts, based on device state.
     */
    double getCurrentPowerConsumption();
}
