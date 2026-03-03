package psms;

/**
 * Emergency Handler (SAD Section 3)
 * Monitors Power Status Monitor and Manual Override Switch inputs.
 * Signals the System Controller to transition to Emergency/Fail-Safe state
 * via processEvent(), keeping all state transitions centralized.
 *
 * Variables: boolean powerFailure, boolean manualOverride
 * Methods: monitorInputs(), triggerEmergency(), resolveEmergency()
 */
public class EmergencyHandler {
    private boolean powerFailure;
    private boolean manualOverride;
    private final SystemController systemController;

    public EmergencyHandler(SystemController systemController) {
        this.systemController = systemController;
        this.powerFailure = false;
        this.manualOverride = false;
    }

    public void monitorInputs() {
        if (powerFailure || manualOverride) {
            triggerEmergency();
        }
    }

    public void triggerEmergency() {
        systemController.processEvent("EMERGENCY_TRIGGER");
    }

    public void resolveEmergency() {
        this.powerFailure = false;
        this.manualOverride = false;
        systemController.processEvent("EMERGENCY_RESOLVE");
    }

    public void setPowerFailure(boolean failure) {
        this.powerFailure = failure;
    }

    public void setManualOverride(boolean override) {
        this.manualOverride = override;
    }

    public boolean isPowerFailure() {
        return powerFailure;
    }

    public boolean isManualOverride() {
        return manualOverride;
    }
}
