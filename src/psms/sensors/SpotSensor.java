package psms.sensors;

import psms.SensorInterface;

/**
 * Binary Spot Sensor (SAD Section 2.3 - External Input)
 * Reports occupied/vacant state for a parking spot.
 */
public class SpotSensor implements SensorInterface {
    private boolean isOccupied;

    @Override
    public String normalizeSignal() {
        return isOccupied ? "SPOT_OCCUPIED" : "SPOT_VACANT";
    }

    @Override
    public void routeToController() {
        // Routes normalized event to System Controller
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }
}
