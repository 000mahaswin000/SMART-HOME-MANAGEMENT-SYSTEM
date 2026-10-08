package smarthome.service;

import smarthome.model.*;
import smarthome.interfaces.SensorListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for sensor management, and OBSERVER PATTERN publisher.
 * Any number of SensorListener observers (typically the
 * AutomationEngine) can register via addListener(). Whenever a
 * sensor's simulated value changes, notifyListeners() is called,
 * which pushes the event out to every observer without SensorService
 * needing to know what they do with it (decoupling).
 */
public class SensorService {

    private final Home home;
    private final List<SensorListener> listeners = new ArrayList<>();

    public SensorService(Home home) {
        this.home = home;
    }

    public void addListener(SensorListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(Sensor sensor) {
        for (SensorListener listener : listeners) {
            listener.onSensorEvent(sensor);
        }
    }

    // ---------- Sensor creation ----------

    public static final String[] SENSOR_TYPES = {
            "Temperature", "Motion", "Smoke", "Door", "Light"
    };

    public Sensor createSensor(String sensorType, String sensorName, String roomId) {
        Sensor sensor = switch (sensorType) {
            case "Temperature" -> new TemperatureSensor(sensorName, roomId);
            case "Motion" -> new MotionSensor(sensorName, roomId);
            case "Smoke" -> new SmokeSensor(sensorName, roomId);
            case "Door" -> new DoorSensor(sensorName, roomId);
            case "Light" -> new LightSensor(sensorName, roomId);
            default -> throw new IllegalArgumentException("Unknown sensor type: " + sensorType);
        };
        home.getSensors().put(sensor.getSensorId(), sensor);
        Room room = home.getRooms().get(roomId);
        if (room != null) {
            room.addSensorId(sensor.getSensorId());
        }
        return sensor;
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(home.getSensors().values());
    }

    public int getActiveSensorCount() {
        int count = 0;
        for (Sensor s : home.getSensors().values()) {
            if (s.isActive()) count++;
        }
        return count;
    }

    // ---------- Simulation methods: each updates state then notifies observers ----------

    private String roomLabel(Sensor sensor) {
        Room room = home.getRooms().get(sensor.getRoomId());
        return room != null ? room.getRoomName() : "Unassigned";
    }

    /** Simulate a new temperature reading on the given sensor. */
    public void simulateTemperature(TemperatureSensor sensor, double newTemperature) {
        sensor.setTemperatureCelsius(newTemperature);
        home.addLog(SystemLog.EventType.SENSOR,
                "Temperature set to " + newTemperature + "\u00B0C in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Simulate a motion trigger on the given sensor. */
    public void triggerMotion(MotionSensor sensor) {
        sensor.setMotionDetected(true);
        home.addLog(SystemLog.EventType.SENSOR, "Motion detected in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Reset a motion sensor back to normal. */
    public void clearMotion(MotionSensor sensor) {
        sensor.setMotionDetected(false);
        home.addLog(SystemLog.EventType.SENSOR, "Motion cleared in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Simulate a smoke trigger on the given sensor. */
    public void triggerSmoke(SmokeSensor sensor) {
        sensor.setSmokeDetected(true);
        home.addLog(SystemLog.EventType.SENSOR, "Smoke detected in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Reset a smoke sensor back to normal. */
    public void clearSmoke(SmokeSensor sensor) {
        sensor.setSmokeDetected(false);
        home.addLog(SystemLog.EventType.SENSOR, "Smoke cleared in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Simulate opening a door. */
    public void openDoor(DoorSensor sensor) {
        sensor.setOpen(true);
        home.addLog(SystemLog.EventType.SENSOR, "Door opened in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Simulate closing a door. */
    public void closeDoor(DoorSensor sensor) {
        sensor.setOpen(false);
        home.addLog(SystemLog.EventType.SENSOR, "Door closed in " + roomLabel(sensor));
        notifyListeners(sensor);
    }

    /** Simulate a new ambient light level reading. */
    public void changeLightLevel(LightSensor sensor, int newLevel) {
        sensor.setLightLevel(newLevel);
        home.addLog(SystemLog.EventType.SENSOR, "Light level set to " + newLevel + "% in " + roomLabel(sensor));
        notifyListeners(sensor);
    }
}
