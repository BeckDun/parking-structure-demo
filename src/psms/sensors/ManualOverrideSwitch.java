package psms.sensors;

import psms.SensorInterface;

/**
 * Manual Override Switch - Administrative control for emergency mode.
 */
public class ManualOverrideSwitch implements SensorInterface {
    private boolean activated;

    @Override
    public void detectChange() {
        this.activated = !this.activated;
    }

    public boolean isActivated() {
        return activated;
    }
}
