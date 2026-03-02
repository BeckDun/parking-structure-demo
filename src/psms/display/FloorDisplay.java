package psms.display;

/**
 * Floor Display (SAD Section 2.3 - Output Device)
 * Per-floor digital display showing available spot count.
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
