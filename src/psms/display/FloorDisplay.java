package psms.display;

/**
 * Floor Display - Shows available spots per floor.
 */
public class FloorDisplay {
    private int availableCount;

    public void updateCount(int count) {
        this.availableCount = count;
    }

    public int getAvailableCount() {
        return availableCount;
    }
}
