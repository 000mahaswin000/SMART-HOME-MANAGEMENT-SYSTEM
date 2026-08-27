package smarthome.model;

/**
 * Concrete device: SmartLock.
 * Uses "isOn" (inherited) to represent power/connectivity, and a
 * separate "locked" flag for the actual lock state, since a smart
 * lock can be powered but unlocked, or vice versa in some designs.
 * Here, for simplicity in this simulator, the lock is always
 * considered powered; ON/OFF toggles between UNLOCKED/LOCKED.
 */
public class SmartLock extends Device {

    private static final long serialVersionUID = 1L;
    private static final double WATTS_IDLE = 1.0;

    private boolean locked;

    public SmartLock(String deviceName, String roomId) {
        super(deviceName, roomId);
        this.locked = true; // secure by default
        super.turnOn(); // lock is always powered/connected
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public String getDeviceType() {
        return "SmartLock";
    }

    @Override
    public double getCurrentPowerConsumption() {
        return WATTS_IDLE;
    }

    @Override
    public String getStatusSummary() {
        return locked ? "LOCKED" : "UNLOCKED";
    }
}
