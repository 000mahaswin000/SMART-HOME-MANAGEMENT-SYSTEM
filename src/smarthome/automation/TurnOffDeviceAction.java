package smarthome.automation;

import smarthome.model.Device;
import smarthome.model.Home;

/**
 * Action that turns a specific target device OFF, identified by device ID.
 */
public class TurnOffDeviceAction extends Action {

    private static final long serialVersionUID = 1L;

    private final String targetDeviceId;
    private final String targetDeviceName;

    public TurnOffDeviceAction(String targetDeviceId, String targetDeviceName) {
        this.targetDeviceId = targetDeviceId;
        this.targetDeviceName = targetDeviceName;
    }

    @Override
    public void execute(Home home, AutomationEngine automationEngine) {
        Device device = home.getDevices().get(targetDeviceId);
        if (device != null) {
            device.turnOff();
            automationEngine.logAutomationEvent("Automation turned OFF: " + device.getDeviceName());
        }
    }

    @Override
    public String describe() {
        return "Turn OFF " + targetDeviceName;
    }
}
