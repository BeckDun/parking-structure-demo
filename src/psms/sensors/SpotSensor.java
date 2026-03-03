package psms.sensors;

/**
 * Binary Spot Sensor (SAD Section 2.3 - External Input Device)
 * Reports occupied/vacant state for a parking spot.
 * Raw signal is read by Sensor Driver and forwarded to Sensor Interface.
 */
public class SpotSensor {
    private boolean isOccupied;

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }
}
