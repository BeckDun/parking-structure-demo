package psms.sensors;

/**
 * Manual Override Switch (SAD Section 2.3 - External Input Device)
 * Administrative control for placing structure into emergency mode.
 * Raw signal is read by Sensor Driver and forwarded to Sensor Interface.
 */
public class ManualOverrideSwitch {
    private boolean activated;

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
