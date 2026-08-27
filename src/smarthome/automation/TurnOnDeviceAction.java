package smarthome.automation;

import smarthome.model.Device;
import smarthome.model.Home;

/**
 * Action that turns a specific target device ON, identified by device ID.
 */
public class TurnOnDeviceAction extends Action {

    private static final long serialVersionUID = 1L;

    private final String targetDeviceId;
    private final String targetDeviceName; // kept for readable descriptions even if device later removed

    public TurnOnDeviceAction(String targetDeviceId, String targetDeviceName) {
        this.targetDeviceId = targetDeviceId;
        this.targetDeviceName = targetDeviceName;
    }

    @Override
    public void execute(Home home, AutomationEngine automationEngine) {
        Device device = home.getDevices().get(targetDeviceId);
        if (device != null) {
            device.turnOn();
            automationEngine.logAutomationEvent("Automation turned ON: " + device.getDeviceName());
        }
    }

    @Override
    public String describe() {
        return "Turn ON " + targetDeviceName;
    }
}
