package psms.drivers;

import psms.display.EntranceDisplay;

/**
 * Display Driver (SAD Section 2.3)
 * Translates presentation commands from the Display Manager into hardware
 * protocols for the physical display devices. Maintains an internal buffer
 * for visual continuity.
 *
 * Talks only to: External Output Devices (EntranceDisplay, FloorDisplay, StallLight)
 */
public class DisplayDriver {
    private final EntranceDisplay entranceDisplay;

    public DisplayDriver(EntranceDisplay entranceDisplay) {
        this.entranceDisplay = entranceDisplay;
    }

    /**
     * Writes data to a physical display device identified by deviceId.
     * Converts the presentation command into the appropriate hardware protocol.
     */
    public void writeToDisplay(String deviceId, String data) {
        switch (deviceId) {
            case "ENTRANCE":
                entranceDisplay.setMessage(data);
                break;
        }
    }
}
