package psms.sensors;

import psms.SensorInterface;

/**
 * Entry Gate Sensor - Detects vehicles arriving at the entrance.
 */
public class EntryGateSensor implements SensorInterface {
    private boolean vehicleDetected;

    @Override
    public void detectChange() {
        this.vehicleDetected = !this.vehicleDetected;
    }

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }
}
