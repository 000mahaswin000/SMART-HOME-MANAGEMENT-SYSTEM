package smarthome.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a security or system alert.
 */
public class Alert implements Serializable {

    private static final long serialVersionUID = -6340023251709180391L;
    private static int ID_COUNTER = 1;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Advances the next ID beyond an ID restored from saved data. */
    public static synchronized void updateCounterFromId(String alertId) {
        if (alertId == null || !alertId.startsWith("ALT-")) {
            return;
        }
        try {
            int id = Integer.parseInt(alertId.substring(4));
            if (id >= ID_COUNTER) {
                ID_COUNTER = id + 1;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs and leave the counter unchanged.
        }
    }

    private static synchronized String nextId() {
        return "ALT-" + ID_COUNTER++;
    }

    /**
     * Nested enum for type-safe severity levels.
     */
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
        this.alertId = nextId();
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
