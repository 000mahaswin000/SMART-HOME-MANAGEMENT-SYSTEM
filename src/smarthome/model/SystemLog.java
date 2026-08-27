package smarthome.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A single entry in the system event log.
 */
public class SystemLog implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Nested enum categorising the type of event. */
    public enum EventType {
        DEVICE, SENSOR, AUTOMATION, SCHEDULE, SECURITY, SYSTEM
    }

    private final LocalDateTime timestamp;
    private final EventType eventType;
    private final String description;

    public SystemLog(EventType eventType, String description) {
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(TIME_FORMAT);
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getFormattedTimestamp() + " - " + eventType + " - " + description;
    }
}
