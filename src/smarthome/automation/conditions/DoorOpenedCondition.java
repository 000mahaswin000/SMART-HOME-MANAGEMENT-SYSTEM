package smarthome.automation.conditions;

import smarthome.automation.Condition;
import smarthome.model.DoorSensor;
import smarthome.model.Home;
import smarthome.model.Sensor;

/**
 * True if ANY door sensor in the home is currently open,
 * optionally requiring security mode to be ON.
 */
public class DoorOpenedCondition extends Condition {

    private static final long serialVersionUID = 1L;

    private final boolean requireSecurityMode;

    public DoorOpenedCondition(boolean requireSecurityMode) {
        this.requireSecurityMode = requireSecurityMode;
    }

    @Override
    public boolean evaluate(Home home) {
        if (requireSecurityMode && !home.isSecurityModeOn()) {
            return false;
        }
        for (Sensor sensor : home.getSensors().values()) {
            if (sensor instanceof DoorSensor doorSensor && sensor.isActive()) {
                if (doorSensor.isOpen()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return "Door opened" + (requireSecurityMode ? " AND security mode ON" : "");
    }
}
