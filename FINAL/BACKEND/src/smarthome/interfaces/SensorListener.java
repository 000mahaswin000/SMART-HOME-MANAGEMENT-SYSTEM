package smarthome.interfaces;

import smarthome.model.Sensor;

public interface SensorListener {

    /**
     * Called whenever a sensor's value or state changes.
     *
     * @param sensor the sensor that changed
     */
    void onSensorEvent(Sensor sensor);
}
