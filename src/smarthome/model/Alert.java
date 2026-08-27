package smarthome.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a security or system alert.
 */
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Nested enum for type-safe severity levels. */
    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    private final String alertId;
    private final String type;
    private final String message;
    private final Severity severity;
    private final LocalDateTime timestamp;
    private boolean read;

    public Alert(String type, String message, Severity severity) {
        this.alertId = "ALT-" + ID_COUNTER.getAndIncrement();
        this.type = type;
        this.message = message;
        this.severity = severity;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public String getAlertId() {
        return alertId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(TIME_FORMAT);
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + type + ": " + message;
    }
}
