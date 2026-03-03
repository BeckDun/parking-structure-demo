package psms;

import psms.drivers.DisplayDriver;

/**
 * Display Manager (SAD Section 3)
 * Controls all visual output devices: entrance display, per-floor displays,
 * and overhead spot indicator lights. Forwards all presentation commands
 * through the Display Driver to the physical hardware.
 *
 * Talks only to: Display Driver
 *
 * Variables: String entranceMessage
 * Methods: updateStallLights(StallID, Color), updateFloorDisplay(int, int), setEntranceSign(String)
 */
public class DisplayManager {
    private final DisplayDriver displayDriver;
    private String entranceMessage;

    public DisplayManager(DisplayDriver displayDriver) {
        this.displayDriver = displayDriver;
        this.entranceMessage = "";
    }

    public void setEntranceSign(String message) {
        this.entranceMessage = message;
        displayDriver.writeToDisplay("ENTRANCE", message);
    }

    public void updateStallLights(int floor, int spotId, String color) {
        displayDriver.writeToDisplay("STALL_" + floor + "_" + spotId, color);
    }

    public void updateFloorDisplay(int floor, int count) {
        displayDriver.writeToDisplay("FLOOR_" + floor, String.valueOf(count));
    }

    public String getEntranceMessage() {
        return entranceMessage;
    }
}
