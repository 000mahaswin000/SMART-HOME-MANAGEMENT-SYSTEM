package smarthome.service;

import smarthome.automation.Action;
import smarthome.automation.AutomationEngine;
import smarthome.automation.Condition;
import smarthome.model.AutomationRule;
import smarthome.model.Home;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for automation rule management (create/enable/disable/delete).
 * Delegates rule evaluation to AutomationEngine.
 */
public class AutomationService {

    private final Home home;
    private final AutomationEngine automationEngine;

    public AutomationService(Home home, AutomationEngine automationEngine) {
        this.home = home;
        this.automationEngine = automationEngine;
    }

    public AutomationRule createRule(String ruleName, Condition condition, Action action) {
        AutomationRule rule = new AutomationRule(ruleName, condition, action);
        home.getAutomationRules().put(rule.getRuleId(), rule);
        return rule;
    }

    public void deleteRule(String ruleId) {
        home.getAutomationRules().remove(ruleId);
    }

    public void setRuleEnabled(String ruleId, boolean enabled) {
        AutomationRule rule = home.getAutomationRules().get(ruleId);
        if (rule != null) {
            rule.setEnabled(enabled);
        }
    }

    public List<AutomationRule> getAllRules() {
        return new ArrayList<>(home.getAutomationRules().values());
    }

    public int getActiveRuleCount() {
        int count = 0;
        for (AutomationRule r : home.getAutomationRules().values()) {
            if (r.isEnabled()) count++;
        }
        return count;
    }

    public AutomationEngine getAutomationEngine() {
        return automationEngine;
    }
}
