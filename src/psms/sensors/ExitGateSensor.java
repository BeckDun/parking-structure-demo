package psms.sensors;

/**
 * Exit Gate Sensor (SAD Section 2.3 - External Input Device)
 * Detects vehicles leaving the structure and reports departures.
 * Raw signal is read by Sensor Driver and forwarded to Sensor Interface.
 */
public class ExitGateSensor {
    private boolean vehicleDetected;

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }

    public void setVehicleDetected(boolean detected) {
        this.vehicleDetected = detected;
    }
}
