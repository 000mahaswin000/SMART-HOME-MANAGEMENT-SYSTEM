package smarthome.interfaces;

import smarthome.model.Alert;

/**
 * Contract for any component capable of raising a system alert
 * (e.g. AutomationEngine, SecurityService).
 * Demonstrates INTERFACE segregation - only alert-raising behaviour.
 */
public interface Alertable {

    /**
     * Raise a new alert into the system.
     *
     * @param alert the alert to raise
     */
    void raiseAlert(Alert alert);
}
