package psms;

/**
 * Emergency Handler - Monitors power status and manual override inputs.
 * Signals the System Controller to transition to Emergency state when triggered.
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

    public void triggerEmergency() {
        systemController.transitionState(SystemState.EMERGENCY);
    }

    public void resolveEmergency() {
        this.powerFailure = false;
        this.manualOverride = false;
        systemController.transitionState(SystemState.STARTUP);
    }

    public boolean isPowerFailure() {
        return powerFailure;
    }

    public boolean isManualOverride() {
        return manualOverride;
    }
}
