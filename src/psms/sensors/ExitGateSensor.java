package psms.sensors;

import psms.SensorInterface;

/**
 * Exit Gate Sensor - Detects vehicles leaving the structure.
 */
public class ExitGateSensor implements SensorInterface {
    private boolean vehicleDetected;

    @Override
    public void detectChange() {
        this.vehicleDetected = !this.vehicleDetected;
    }

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }

    public void reportDeparture() {
        this.vehicleDetected = false;
    }
}
