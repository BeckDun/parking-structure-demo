package psms.model;

/**
 * Represents an individual parking spot.
 */
public class ParkingSpot {
    private final int spotId;
    private final int floorNumber;
    private final boolean isHandicap;
    private boolean isOccupied;

    public ParkingSpot(int spotId, int floorNumber, boolean isHandicap) {
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.isHandicap = isHandicap;
        this.isOccupied = false;
    }

    public int getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isHandicap() {
        return isHandicap;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public SpotStatus getStatus() {
        if (isOccupied) {
            return SpotStatus.OCCUPIED;
        }
        return isHandicap ? SpotStatus.HANDICAP_AVAILABLE : SpotStatus.AVAILABLE;
    }
}
