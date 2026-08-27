package smarthome.automation;

import smarthome.model.Alert;
import smarthome.model.Home;

/**
 * Action that raises a system/security alert of a given type,
 * message and severity.
 */
public class RaiseAlertAction extends Action {

    private static final long serialVersionUID = 1L;

    private final String alertType;
    private final String alertMessage;
    private final Alert.Severity severity;

    public RaiseAlertAction(String alertType, String alertMessage, Alert.Severity severity) {
        this.alertType = alertType;
        this.alertMessage = alertMessage;
        this.severity = severity;
    }

    @Override
    public void execute(Home home, AutomationEngine automationEngine) {
        Alert alert = new Alert(alertType, alertMessage, severity);
        home.getAlerts().put(alert.getAlertId(), alert);
        automationEngine.raiseAlert(alert);
        automationEngine.logAutomationEvent("Alert raised: " + alert);
    }

    @Override
    public String describe() {
        return "Create " + severity + " alert: " + alertMessage;
    }
}
