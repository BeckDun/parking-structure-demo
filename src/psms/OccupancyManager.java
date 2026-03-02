package psms;

import psms.model.ParkingFloor;

import java.util.List;

/**
 * Occupancy Manager (SAD Section 3)
 * Maintains vehicle counts at per-spot, per-floor, and structure levels.
 * Tracks in-transit vehicles (entered gate but not yet parked, or left spot but not yet exited).
 *
 * Variables: int totalCapacity, int currentCount, int handicapCount, int inTransitCount
 * Methods: updateCount(boolean, boolean), isFull(), getFloorAvailability(int)
 */
public class OccupancyManager {
    private int totalCapacity;
    private int currentCount;
    private int handicapCount;
    private int inTransitCount;

    public OccupancyManager() {
        this.totalCapacity = 0;
        this.currentCount = 0;
        this.handicapCount = 0;
        this.inTransitCount = 0;
    }

    public void reset(int capacity) {
        this.totalCapacity = capacity;
        this.currentCount = 0;
        this.handicapCount = 0;
        this.inTransitCount = 0;
    }

    public void setCurrentCount(int count) {
        this.currentCount = count;
    }

    public void updateCount(boolean isEntering, boolean isHandicap) {
        if (isEntering) {
            currentCount++;
            if (isHandicap) handicapCount++;
        } else {
            currentCount = Math.max(0, currentCount - 1);
            if (isHandicap) handicapCount = Math.max(0, handicapCount - 1);
        }
    }

    public void vehicleEntered() {
        currentCount++;
        inTransitCount++;
    }

    public void vehicleParked() {
        inTransitCount = Math.max(0, inTransitCount - 1);
    }

    public void vehicleUnparked() {
        inTransitCount++;
    }

    public void vehicleExited() {
        currentCount = Math.max(0, currentCount - 1);
        inTransitCount = Math.max(0, inTransitCount - 1);
    }

    public boolean isFull() {
        return currentCount >= totalCapacity;
    }

    public int getFloorAvailability(int floor, List<ParkingFloor> floors) {
        if (floor < 1 || floor > floors.size()) return 0;
        return floors.get(floor - 1).getAvailableSpots();
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getHandicapCount() {
        return handicapCount;
    }

    public int getInTransitCount() {
        return inTransitCount;
    }

    public int getParkedCount() {
        return Math.max(0, currentCount - inTransitCount);
    }

    public int getAvailableSpots() {
        return Math.max(0, totalCapacity - currentCount);
    }
}
