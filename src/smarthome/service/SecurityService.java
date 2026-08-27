package smarthome.service;

import smarthome.model.Home;
import smarthome.model.SystemLog;

/**
 * Service layer for the home security mode toggle.
 * Automation rules (e.g. MotionDetectedCondition, DoorOpenedCondition)
 * read Home.isSecurityModeOn() directly, so simply flipping this flag
 * is enough to change automation behaviour throughout the system.
 */
public class SecurityService {

    private final Home home;

    public SecurityService(Home home) {
        this.home = home;
    }

    public boolean isSecurityModeOn() {
        return home.isSecurityModeOn();
    }

    public void setSecurityMode(boolean on) {
        home.setSecurityModeOn(on);
        home.addLog(SystemLog.EventType.SECURITY, "Security mode turned " + (on ? "ON" : "OFF"));
    }

    public void toggleSecurityMode() {
        setSecurityMode(!home.isSecurityModeOn());
    }
}
