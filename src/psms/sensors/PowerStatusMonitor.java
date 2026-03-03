package psms.sensors;

/**
 * Power Status Monitor (SAD Section 2.3 - External Input Device)
 * Detects power failures and reports to Emergency Handler.
 * Raw signal is read by Sensor Driver and forwarded to Sensor Interface.
 */
public class PowerStatusMonitor {
    private boolean powerAvailable = true;

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void setPowerAvailable(boolean available) {
        this.powerAvailable = available;
    }
}
