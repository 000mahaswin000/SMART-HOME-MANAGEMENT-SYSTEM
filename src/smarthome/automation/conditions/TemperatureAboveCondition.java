package smarthome.automation.conditions;

import smarthome.automation.Condition;
import smarthome.model.Home;
import smarthome.model.Sensor;
import smarthome.model.TemperatureSensor;

/**
 * True if ANY temperature sensor in the home reads above the threshold.
 */
public class TemperatureAboveCondition extends Condition {

    private static final long serialVersionUID = 1L;

    private final double thresholdCelsius;

    public TemperatureAboveCondition(double thresholdCelsius) {
        this.thresholdCelsius = thresholdCelsius;
    }

    @Override
    public boolean evaluate(Home home) {
        for (Sensor sensor : home.getSensors().values()) {
            if (sensor instanceof TemperatureSensor tempSensor && sensor.isActive()) {
                if (tempSensor.getTemperatureCelsius() > thresholdCelsius) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return "Temperature > " + thresholdCelsius + "\u00B0C";
    }
}
