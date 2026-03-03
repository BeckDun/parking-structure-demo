package psms;

/**
 * Sensor Interface (SAD Section 3)
 * Abstraction layer between Sensor Drivers and the System Controller.
 * Normalizes processed digital signals from Sensor Drivers into
 * standardized event messages, then routes them to the System Controller.
 *
 * Variables: String currentSignal, String normalizedEvent
 * Methods: normalizeSignal(RawSignal), routeToController(Event)
 */
public class SensorInterface {
    private final SystemController systemController;
    private String currentSignal;
    private String normalizedEvent;

    public SensorInterface(SystemController systemController) {
        this.systemController = systemController;
    }

    /**
     * Normalizes a processed signal from the Sensor Driver into a
     * standardized event string for the System Controller.
     *
     * Raw signal format: "DEVICE_TYPE:ACTIVE" or "DEVICE_TYPE:INACTIVE"
     */
    public String normalizeSignal(String rawSignal) {
        this.currentSignal = rawSignal;
        if (rawSignal == null) {
            this.normalizedEvent = null;
            return null;
        }

        String[] parts = rawSignal.split(":");
        if (parts.length < 2) {
            this.normalizedEvent = null;
            return null;
        }

        String device = parts[0];
        boolean active = "ACTIVE".equals(parts[1]);

        switch (device) {
            case "ENTRY_GATE":
                this.normalizedEvent = active ? "VEHICLE_ENTRY" : null;
                break;
            case "EXIT_GATE":
                this.normalizedEvent = active ? "VEHICLE_EXIT" : null;
                break;
            case "SPOT_SENSOR":
                this.normalizedEvent = active ? "SPOT_OCCUPIED" : "SPOT_VACANT";
                break;
            case "POWER_STATUS":
                this.normalizedEvent = active ? null : "POWER_FAILURE";
                break;
            case "MANUAL_OVERRIDE":
                this.normalizedEvent = active ? "OVERRIDE_ACTIVE" : null;
                break;
            default:
                this.normalizedEvent = null;
        }
        return this.normalizedEvent;
    }

    /**
     * Routes a normalized event to the System Controller for processing.
     * Returns the result from the System Controller's event processing.
     */
    public boolean routeToController(String event) {
        if (event == null) return false;
        return systemController.processEvent(event);
    }

    public String getCurrentSignal() {
        return currentSignal;
    }

    public String getNormalizedEvent() {
        return normalizedEvent;
    }
}
