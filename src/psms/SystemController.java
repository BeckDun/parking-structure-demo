package psms;

/**
 * System Controller (SAD Section 3)
 * Central coordinator with state machine: STARTUP, NORMAL, AT_CAPACITY, EMERGENCY.
 * Receives normalized events from the Sensor Interface, evaluates them based on
 * currentState, and dispatches commands to subsystem components.
 *
 * Variables: SystemState currentState
 * Methods: transitionState(SystemState), processEvent(Event)
 */
public class SystemController {
    private SystemState currentState;
    private EntryGateController entryGateController;
    private OccupancyManager occupancyManager;
    private DisplayManager displayManager;

    public SystemController() {
        this.currentState = SystemState.STARTUP;
    }

    /**
     * Wires this controller to the subsystem components it commands.
     * Called after all components are constructed to avoid circular dependencies.
     */
    public void setSubsystems(EntryGateController entryGateController,
                              OccupancyManager occupancyManager,
                              DisplayManager displayManager) {
        this.entryGateController = entryGateController;
        this.occupancyManager = occupancyManager;
        this.displayManager = displayManager;
    }

    /**
     * Central event processor (SAD Section 3).
     * Evaluates the event based on currentState and dispatches commands
     * to EntryGateController, OccupancyManager, and DisplayManager.
     *
     * @return true if the event was processed successfully
     */
    public boolean processEvent(String event) {
        switch (event) {
            case "VEHICLE_ENTRY":
                if (currentState == SystemState.EMERGENCY) return false;
                if (occupancyManager.getAvailableSpots() <= 0) {
                    transitionState(SystemState.AT_CAPACITY);
                    entryGateController.lockGate();
                    refreshDisplay();
                    return false;
                }
                entryGateController.openGate();
                occupancyManager.vehicleEntered();
                evaluateState();
                refreshDisplay();
                return true;

            case "GATE_CLOSE":
                entryGateController.closeGate();
                return true;

            case "SPOT_OCCUPIED":
                occupancyManager.vehicleParked();
                evaluateState();
                refreshDisplay();
                return true;

            case "SPOT_VACANT":
                occupancyManager.vehicleUnparked();
                evaluateState();
                refreshDisplay();
                return true;

            case "VEHICLE_EXIT":
                occupancyManager.vehicleExited();
                evaluateState();
                refreshDisplay();
                return true;

            case "EMERGENCY_TRIGGER":
                transitionState(SystemState.EMERGENCY);
                entryGateController.lockGate();
                refreshDisplay();
                return true;

            case "EMERGENCY_RESOLVE":
                transitionState(SystemState.STARTUP);
                return true;

            default:
                return false;
        }
    }

    public void transitionState(SystemState newState) {
        this.currentState = newState;
    }

    public SystemState getCurrentState() {
        return currentState;
    }

    /**
     * Re-evaluates the system state based on current occupancy.
     * Transitions between NORMAL and AT_CAPACITY as needed.
     */
    private void evaluateState() {
        if (currentState == SystemState.EMERGENCY) return;

        if (occupancyManager.getAvailableSpots() == 0) {
            transitionState(SystemState.AT_CAPACITY);
            entryGateController.lockGate();
        } else if (currentState == SystemState.AT_CAPACITY) {
            transitionState(SystemState.NORMAL);
            entryGateController.closeGate();
        }
    }

    /**
     * Updates the Display Manager's entrance sign based on current state.
     * Can also be called externally after procedural operations (startup, reset).
     */
    public void refreshDisplay() {
        if (displayManager == null) return;
        int available = occupancyManager.getAvailableSpots();
        if (currentState == SystemState.EMERGENCY) {
            displayManager.setEntranceSign("CLOSED - EMERGENCY");
        } else if (available == 0) {
            displayManager.setEntranceSign("FULL");
        } else {
            displayManager.setEntranceSign(available + " SPOTS AVAILABLE");
        }
    }
}
