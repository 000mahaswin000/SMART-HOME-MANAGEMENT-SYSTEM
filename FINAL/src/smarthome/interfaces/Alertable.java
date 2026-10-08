package smarthome.interfaces;

import smarthome.model.Alert;

public interface Alertable {

    void raiseAlert(Alert alert);
}
