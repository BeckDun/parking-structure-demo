package psms.sensors;

import psms.SensorInterface;

/**
 * Binary Spot Sensor - Reports occupied/vacant state for a parking spot.
 */
public class SpotSensor implements SensorInterface {
    private boolean isOccupied;

    @Override
    public void detectChange() {
        this.isOccupied = !this.isOccupied;
    }

    public boolean isOccupied() {
        return isOccupied;
    }
}
