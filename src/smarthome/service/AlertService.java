package smarthome.service;

import smarthome.model.Alert;
import smarthome.model.Home;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service layer for alert management (mark as read / clear).
 * New alerts are created by RaiseAlertAction as part of the
 * automation flow, not directly by this service; this service
 * handles the read/list/clear side of the alert lifecycle.
 */
public class AlertService {

    private final Home home;

    public AlertService(Home home) {
        this.home = home;
    }

    public void markAsRead(String alertId) {
        Alert alert = home.getAlerts().get(alertId);
        if (alert != null) {
            alert.markAsRead();
        }
    }

    public void clearAllAlerts() {
        home.getAlerts().clear();
    }

    public void clearAlert(String alertId) {
        home.getAlerts().remove(alertId);
    }

    /** Alerts sorted newest-first for display. */
    public List<Alert> getAllAlertsSorted() {
        List<Alert> alerts = new ArrayList<>(home.getAlerts().values());
        alerts.sort(Comparator.comparing(Alert::getTimestamp).reversed());
        return alerts;
    }

    public int getUnreadAlertCount() {
        int count = 0;
        for (Alert a : home.getAlerts().values()) {
            if (!a.isRead()) count++;
        }
        return count;
    }
}
