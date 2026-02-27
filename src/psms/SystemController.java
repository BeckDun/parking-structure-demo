package psms;

/**
 * System Controller - Central coordinator implementing a state machine with four states:
 * STARTUP, NORMAL, AT_CAPACITY, and EMERGENCY.
 */
public class SystemController {
    private SystemState currentState;

    public SystemController() {
        this.currentState = SystemState.STARTUP;
    }

    public void transitionState(SystemState newState) {
        this.currentState = newState;
    }

    public SystemState getCurrentState() {
        return currentState;
    }
}
