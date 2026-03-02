package psms.sensors;

import psms.SensorInterface;

/**
 * Power Status Monitor (SAD Section 2.3 - External Input)
 * Detects power failures and reports to Emergency Handler.
 */
public class PowerStatusMonitor implements SensorInterface {
    private boolean powerAvailable = true;

    @Override
    public String normalizeSignal() {
        return powerAvailable ? "POWER_OK" : "POWER_FAILURE";
    }

    @Override
    public void routeToController() {
        // Routes normalized event to System Controller
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void setPowerAvailable(boolean available) {
        this.powerAvailable = available;
    }
}
