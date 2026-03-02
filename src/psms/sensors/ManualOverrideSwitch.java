package psms.sensors;

import psms.SensorInterface;

/**
 * Manual Override Switch (SAD Section 2.3 - External Input)
 * Administrative control for placing structure into emergency mode.
 */
public class ManualOverrideSwitch implements SensorInterface {
    private boolean activated;

    @Override
    public String normalizeSignal() {
        return activated ? "OVERRIDE_ACTIVE" : "OVERRIDE_INACTIVE";
    }

    @Override
    public void routeToController() {
        // Routes normalized event to System Controller
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
