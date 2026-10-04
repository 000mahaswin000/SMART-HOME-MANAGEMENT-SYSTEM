package smarthome.interfaces;

import smarthome.model.Alert;

public interface Alertable {

    /**
     * Raise a new alert into the system.
     *
     * @param alert the alert to raise
     */
    void raiseAlert(Alert alert);
}
