package psms.display;

import psms.model.SpotStatus;

/**
 * Stall Light - Overhead LED indicator (green/red/blue).
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
