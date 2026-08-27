package smarthome.model;

import smarthome.automation.Action;
import smarthome.automation.Condition;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single "IF condition THEN action" automation rule.
 * The rule itself holds a Condition and an Action (STRATEGY PATTERN):
 * it does not know HOW to evaluate the condition or perform the
 * action - it just delegates to the strategy objects it holds.
 */
public class AutomationRule implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    private final String ruleId;
    private String ruleName;
    private final Condition condition;
    private final Action action;
    private boolean enabled;
    private final LocalDateTime creationTime;

    public AutomationRule(String ruleName, Condition condition, Action action) {
        if (ruleName == null || ruleName.isBlank()) {
            throw new IllegalArgumentException("Rule name cannot be empty");
        }
        this.ruleId = "RULE-" + ID_COUNTER.getAndIncrement();
        this.ruleName = ruleName.trim();
        this.condition = condition;
        this.action = action;
        this.enabled = true;
        this.creationTime = LocalDateTime.now();
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        if (ruleName == null || ruleName.isBlank()) {
            throw new IllegalArgumentException("Rule name cannot be empty");
        }
        this.ruleName = ruleName.trim();
    }

    public Condition getCondition() {
        return condition;
    }

    public Action getAction() {
        return action;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public String getRuleDescription() {
        return "IF " + condition.describe() + " THEN " + action.describe();
    }

    @Override
    public String toString() {
        return ruleName + " (" + (enabled ? "enabled" : "disabled") + "): " + getRuleDescription();
    }
}
