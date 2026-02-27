package psms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a floor in the parking structure.
 */
public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber, int totalSpots, int handicapSpots) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();

        for (int i = 0; i < handicapSpots; i++) {
            spots.add(new ParkingSpot(i, floorNumber, true));
        }
        for (int i = handicapSpots; i < totalSpots; i++) {
            spots.add(new ParkingSpot(i, floorNumber, false));
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getTotalCapacity() {
        return spots.size();
    }

    public int getAvailableSpots() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied()) count++;
        }
        return count;
    }

    public int getOccupiedSpots() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied()) count++;
        }
        return count;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }
}
