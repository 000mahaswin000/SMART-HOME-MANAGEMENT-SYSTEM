package smarthome.automation;

import smarthome.model.Home;

import java.io.Serializable;

/**
 * STRATEGY PATTERN: Condition is the strategy interface (implemented as
 * an abstract class here so it can hold a shared description field).
 * Each concrete condition (e.g. TemperatureAboveCondition,
 * MotionDetectedCondition) implements evaluate() differently.
 * AutomationRule holds a Condition reference and delegates evaluation
 * to it, allowing conditions to be swapped without changing
 * AutomationRule or AutomationEngine.
 */
public abstract class Condition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Evaluate this condition against the current state of the home.
     *
     * @param home the current application state
     * @return true if the condition is currently satisfied
     */
    public abstract boolean evaluate(Home home);

    /** @return a human-readable description of this condition, e.g. "Temperature > 30". */
    public abstract String describe();
}
