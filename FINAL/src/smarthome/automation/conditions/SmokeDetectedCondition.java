package smarthome.automation.conditions;

import smarthome.automation.Condition;
import smarthome.model.Home;
import smarthome.model.Sensor;
import smarthome.model.SmokeSensor;

/**
 * True if ANY smoke sensor in the home currently detects smoke.
 */
public class SmokeDetectedCondition extends Condition {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean evaluate(Home home) {
        for (Sensor sensor : home.getSensors().values()) {
            if (sensor instanceof SmokeSensor smokeSensor && sensor.isActive()) {
                if (smokeSensor.isSmokeDetected()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return "Smoke detected";
    }
}
