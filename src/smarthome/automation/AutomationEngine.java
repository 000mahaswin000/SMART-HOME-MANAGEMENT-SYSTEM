package smarthome.automation;

import smarthome.interfaces.Alertable;
import smarthome.interfaces.SensorListener;
import smarthome.model.Alert;
import smarthome.model.AutomationRule;
import smarthome.model.Home;
import smarthome.model.Sensor;
import smarthome.model.SystemLog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * OBSERVER PATTERN: AutomationEngine implements SensorListener and is
 * registered with SensorService as an observer. Whenever a sensor
 * event occurs, SensorService notifies AutomationEngine via
 * onSensorEvent(), and the engine evaluates every enabled
 * AutomationRule against the current Home state, executing any
 * whose Condition is satisfied.
 *
 * Also implements Alertable so that Actions (see RaiseAlertAction)
 * can push new alerts through the engine, which forwards them to
 * any registered alert-callback (typically the UI, so the Alerts
 * panel and Dashboard refresh immediately).
 */
public class AutomationEngine implements SensorListener, Alertable {

    private final Home home;
    private final List<Consumer<Alert>> alertCallbacks = new ArrayList<>();
    private final List<Consumer<String>> logCallbacks = new ArrayList<>();

    public AutomationEngine(Home home) {
        this.home = home;
    }

    /** Register a callback to be invoked whenever a new alert is raised (used by the UI). */
    public void addAlertCallback(Consumer<Alert> callback) {
        alertCallbacks.add(callback);
    }

    /** Register a callback to be invoked whenever the engine wants to log an event (used by the UI). */
    public void addLogCallback(Consumer<String> callback) {
        logCallbacks.add(callback);
    }

    /**
     * Called by SensorService (the publisher) whenever any sensor
     * changes state. Demonstrates the OBSERVER PATTERN in action.
     */
    @Override
    public void onSensorEvent(Sensor sensor) {
        evaluateAllRules();
    }

    /** Evaluate every enabled rule against the current home state and fire matching actions. */
    public void evaluateAllRules() {
        List<AutomationRule> rulesSnapshot = new ArrayList<>(home.getAutomationRules().values());
        for (AutomationRule rule : rulesSnapshot) {
            if (!rule.isEnabled()) continue;
            if (rule.getCondition().evaluate(home)) {
                logAutomationEvent("\"" + rule.getRuleName() + "\" rule triggered");
                rule.getAction().execute(home, this);
            }
        }
    }

    @Override
    public void raiseAlert(Alert alert) {
        for (Consumer<Alert> callback : alertCallbacks) {
            callback.accept(alert);
        }
    }

    /** Used by Action implementations (and this engine) to record an automation log entry. */
    public void logAutomationEvent(String description) {
        home.addLog(SystemLog.EventType.AUTOMATION, description);
        for (Consumer<String> callback : logCallbacks) {
            callback.accept(description);
        }
    }
}
