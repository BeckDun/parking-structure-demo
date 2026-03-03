package psms.sensors;

/**
 * Entry Gate Sensor (SAD Section 2.3 - External Input Device)
 * Detects vehicles arriving at the entrance.
 * Raw signal is read by Sensor Driver and forwarded to Sensor Interface.
 */
public class EntryGateSensor {
    private boolean vehicleDetected;

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }

    public void setVehicleDetected(boolean detected) {
        this.vehicleDetected = detected;
    }
}
