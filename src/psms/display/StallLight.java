package psms.display;

import psms.model.SpotStatus;

/**
 * Stall Light (SAD Section 2.3 - Output Device)
 * Overhead LED indicator: green (available), red (occupied), blue (handicap available).
 */
public class StallLight {
    private SpotStatus status = SpotStatus.AVAILABLE;

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public SpotStatus getStatus() {
        return status;
    }
}
