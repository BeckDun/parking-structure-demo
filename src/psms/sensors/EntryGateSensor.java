package psms.sensors;

import psms.SensorInterface;

/**
 * Entry Gate Sensor (SAD Section 3 - External Input)
 * Detects vehicles arriving at the entrance.
 */
public class EntryGateSensor implements SensorInterface {
    private boolean vehicleDetected;

    @Override
    public String normalizeSignal() {
        return vehicleDetected ? "VEHICLE_ENTRY" : "NO_VEHICLE";
    }

    @Override
    public void routeToController() {
        // Routes normalized event to System Controller
    }

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }

    public void setVehicleDetected(boolean detected) {
        this.vehicleDetected = detected;
    }
}
