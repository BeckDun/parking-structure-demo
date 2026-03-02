package psms;

/**
 * System Controller (SAD Section 3)
 * Central coordinator with state machine: STARTUP, NORMAL, AT_CAPACITY, EMERGENCY.
 *
 * Variables: SystemState currentState
 * Methods: transitionState(SystemState), processEvent(Event)
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
