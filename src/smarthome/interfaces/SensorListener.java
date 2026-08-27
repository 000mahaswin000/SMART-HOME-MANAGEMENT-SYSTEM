package smarthome.interfaces;

import smarthome.model.Sensor;

/**
 * Observer interface used by the OBSERVER DESIGN PATTERN.
 * Any class that wants to be notified when a sensor value changes
 * implements this interface and registers itself with the sensor
 * (or with a central publisher such as SensorService).
 */
public interface SensorListener {

    /**
     * Called whenever a sensor's value or state changes.
     *
     * @param sensor the sensor that changed
     */
    void onSensorEvent(Sensor sensor);
}
