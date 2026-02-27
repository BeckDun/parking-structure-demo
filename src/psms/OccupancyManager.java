package psms;

/**
 * Occupancy Manager - Maintains vehicle counts at per-spot, per-floor, and structure levels.
 */
public class OccupancyManager {
    private int totalCapacity;
    private int currentCount;

    public OccupancyManager() {
        this.totalCapacity = 0;
        this.currentCount = 0;
    }

    public void reset(int capacity) {
        this.totalCapacity = capacity;
        this.currentCount = 0;
    }

    public void setCurrentCount(int count) {
        this.currentCount = count;
    }

    public void updateCount(boolean isEntering, boolean isHandicap) {
        if (isEntering) {
            currentCount++;
        } else {
            currentCount = Math.max(0, currentCount - 1);
        }
    }

    public boolean isFull() {
        return currentCount >= totalCapacity;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getAvailableSpots() {
        return Math.max(0, totalCapacity - currentCount);
    }
}
