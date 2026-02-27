package psms;

/**
 * Display Manager - Controls all visual output devices: entrance display,
 * per-floor displays, and overhead spot indicator lights.
 */
public class DisplayManager {
    private String entranceMessage;

    public DisplayManager() {
        this.entranceMessage = "";
    }

    public void setEntranceSign(String message) {
        this.entranceMessage = message;
    }

    public String getEntranceMessage() {
        return entranceMessage;
    }
}
