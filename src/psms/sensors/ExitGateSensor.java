package psms.sensors;

import psms.SensorInterface;

/**
 * Exit Gate Sensor (SAD Section 3)
 * Detects vehicles leaving the structure and reports departures.
 *
 * Variables: boolean vehicleDetected
 * Methods: reportDeparture()
 */
public class ExitGateSensor implements SensorInterface {
    private boolean vehicleDetected;

    @Override
    public String normalizeSignal() {
        return vehicleDetected ? "VEHICLE_EXIT" : "NO_VEHICLE";
    }

    @Override
    public void routeToController() {
        // Routes normalized event to System Controller
    }

    public void reportDeparture() {
        this.vehicleDetected = false;
    }

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }

    public void setVehicleDetected(boolean detected) {
        this.vehicleDetected = detected;
    }
}
