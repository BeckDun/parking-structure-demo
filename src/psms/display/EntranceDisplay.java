package psms.display;

/**
 * Entrance Display (SAD Section 2.3 - Output Device)
 * Shows real-time availability at the structure entrance (e.g. "FULL", "25 Spaces Available").
 */
public class EntranceDisplay {
    private String message = "";

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
