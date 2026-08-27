package smarthome.automation.conditions;

import smarthome.automation.Condition;
import smarthome.model.Home;
import smarthome.model.LightSensor;
import smarthome.model.Sensor;

/**
 * True if ANY light sensor in the home reads below the threshold.
 */
public class LightLevelBelowCondition extends Condition {

    private static final long serialVersionUID = 1L;

    private final int threshold;

    public LightLevelBelowCondition(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean evaluate(Home home) {
        for (Sensor sensor : home.getSensors().values()) {
            if (sensor instanceof LightSensor lightSensor && sensor.isActive()) {
                if (lightSensor.getLightLevel() < threshold) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return "Light level < " + threshold + "%";
    }
}
