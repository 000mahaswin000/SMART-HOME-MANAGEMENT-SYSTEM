package smarthome.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate root of the entire application state.
 * Demonstrates COMPOSITION: Home "owns" Rooms, Devices, Sensors,
 * AutomationRules, Schedules, Alerts and Logs - if the Home is
 * destroyed (e.g. not saved), none of these survive independently.
 * This whole object graph is what gets serialised to disk by
 * FileManager.
 *
 * Uses Map<String, X> collections keyed by ID for O(1) lookup, and a
 * List<SystemLog> for the ordered event log, demonstrating correct
 * use of GENERICS and the Collections Framework.
 */
public class Home implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Log list is capped so a long-running session doesn't grow the save file without bound. */
    private static final int MAX_LOG_ENTRIES = 500;

    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Device> devices = new LinkedHashMap<>();
    private final Map<String, Sensor> sensors = new LinkedHashMap<>();
    private final Map<String, AutomationRule> automationRules = new LinkedHashMap<>();
    private final Map<String, Schedule> schedules = new LinkedHashMap<>();
    private final Map<String, Alert> alerts = new LinkedHashMap<>();
    private final List<SystemLog> logs = new ArrayList<>();

    private boolean securityModeOn = false;

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Device> getDevices() {
        return devices;
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Map<String, AutomationRule> getAutomationRules() {
        return automationRules;
    }

    public Map<String, Schedule> getSchedules() {
        return schedules;
    }

    public Map<String, Alert> getAlerts() {
        return alerts;
    }

    public List<SystemLog> getLogs() {
        return logs;
    }

    /**
     * Append a new log entry, trimming the oldest entry if the log
     * has grown past MAX_LOG_ENTRIES. Centralising this here means
     * every service that holds a Home reference can log consistently
     * without needing a separate LogService.
     */
    public void addLog(SystemLog.EventType eventType, String description) {
        logs.add(new SystemLog(eventType, description));
        while (logs.size() > MAX_LOG_ENTRIES) {
            logs.remove(0);
        }
    }

    public boolean isSecurityModeOn() {
        return securityModeOn;
    }

    public void setSecurityModeOn(boolean securityModeOn) {
        this.securityModeOn = securityModeOn;
    }
}
