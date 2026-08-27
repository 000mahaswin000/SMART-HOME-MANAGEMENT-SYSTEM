package smarthome.automation.conditions;

import smarthome.automation.Condition;
import smarthome.model.Home;
import smarthome.model.MotionSensor;
import smarthome.model.Sensor;

/**
 * True if ANY motion sensor in the home currently detects motion,
 * optionally requiring security mode to be ON.
 */
public class MotionDetectedCondition extends Condition {

    private static final long serialVersionUID = 1L;

    private final boolean requireSecurityMode;

    public MotionDetectedCondition(boolean requireSecurityMode) {
        this.requireSecurityMode = requireSecurityMode;
    }

    @Override
    public boolean evaluate(Home home) {
        if (requireSecurityMode && !home.isSecurityModeOn()) {
            return false;
        }
        for (Sensor sensor : home.getSensors().values()) {
            if (sensor instanceof MotionSensor motionSensor && sensor.isActive()) {
                if (motionSensor.isMotionDetected()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return "Motion detected" + (requireSecurityMode ? " AND security mode ON" : "");
    }
}
