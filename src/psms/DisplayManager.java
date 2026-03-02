package psms;

/**
 * Display Manager (SAD Section 3)
 * Controls all visual output devices: entrance display, per-floor displays,
 * and overhead spot indicator lights.
 *
 * Variables: String entranceMessage
 * Methods: updateStallLights(StallID, Color), updateFloorDisplay(int, int), setEntranceSign(String)
 */
public class DisplayManager {
    private String entranceMessage;

    public DisplayManager() {
        this.entranceMessage = "";
    }

    public void updateStallLights(int floor, int spotId, String color) {
        // Drives physical overhead LED for the given spot
    }

    public void updateFloorDisplay(int floor, int count) {
        // Updates per-floor digital display with available count
    }

    public void setEntranceSign(String message) {
        this.entranceMessage = message;
    }

    public String getEntranceMessage() {
        return entranceMessage;
    }
}
