package smarthome.automation;

import smarthome.model.Home;

import java.io.Serializable;

/**
 * STRATEGY PATTERN: Action is the strategy interface for what happens
 * when a rule's condition is satisfied. Concrete actions (e.g.
 * TurnOnDeviceAction, RaiseAlertAction) implement execute() differently.
 */
public abstract class Action implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Execute this action against the current home state.
     *
     * @param home            the current application state
     * @param automationEngine the engine executing this action, used to
     *                        raise alerts or log events as a side effect
     */
    public abstract void execute(Home home, AutomationEngine automationEngine);

    /** @return a human-readable description of this action, e.g. "Turn ON Living Room AC". */
    public abstract String describe();
}
