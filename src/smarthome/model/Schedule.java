package smarthome.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a scheduled device operation, e.g.
 * "07:00 PM -> Turn ON Living Room Light".
 * Uses java.time.LocalTime for the scheduled time-of-day, and
 * supports an optional daily repeat flag.
 */
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    /** The action a schedule can perform on its target device. */
    public enum ScheduleAction {
        TURN_ON, TURN_OFF
    }

    private final String scheduleId;
    private String scheduleName;
    private final String deviceId;
    private final String deviceName; // kept for display even if device later removed
    private final ScheduleAction action;
    private LocalTime scheduledTime;
    private boolean enabled;
    private final boolean repeatDaily;
    /** Tracks the last date this schedule fired, to avoid double-firing within the same minute/day. */
    private String lastFiredDate;

    public Schedule(String scheduleName, String deviceId, String deviceName,
                     ScheduleAction action, LocalTime scheduledTime, boolean repeatDaily) {
        if (scheduleName == null || scheduleName.isBlank()) {
            throw new IllegalArgumentException("Schedule name cannot be empty");
        }
        if (deviceId == null || scheduledTime == null || action == null) {
            throw new IllegalArgumentException("Schedule requires a device, action and time");
        }
        this.scheduleId = "SCH-" + ID_COUNTER.getAndIncrement();
        this.scheduleName = scheduleName.trim();
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.action = action;
        this.scheduledTime = scheduledTime;
        this.enabled = true;
        this.repeatDaily = repeatDaily;
        this.lastFiredDate = "";
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public ScheduleAction getAction() {
        return action;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRepeatDaily() {
        return repeatDaily;
    }

    public String getLastFiredDate() {
        return lastFiredDate;
    }

    public void setLastFiredDate(String lastFiredDate) {
        this.lastFiredDate = lastFiredDate;
    }

    @Override
    public String toString() {
        return scheduledTime + " -> " + action + " " + deviceName
                + (repeatDaily ? " (daily)" : " (once)");
    }
}
