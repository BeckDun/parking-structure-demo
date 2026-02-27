package psms.sensors;

import psms.SensorInterface;

/**
 * Power Status Monitor - Detects power failures.
 */
public class PowerStatusMonitor implements SensorInterface {
    private boolean powerAvailable = true;

    @Override
    public void detectChange() {
        this.powerAvailable = !this.powerAvailable;
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }
}
