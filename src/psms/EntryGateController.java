package psms;

import psms.model.GateState;

/**
 * Entry Gate Controller (SAD Section 3)
 * Receives open/close commands from System Controller,
 * translates them into actuator signals for the physical entry gate.
 *
 * Variables: boolean isGateOpen
 * Methods: openGate(), closeGate(), handleAccesses(SystemState)
 */
public class EntryGateController {
    private GateState gateState;

    public EntryGateController() {
        this.gateState = GateState.CLOSED;
    }

    public void openGate() {
        this.gateState = GateState.OPEN;
    }

    public void closeGate() {
        this.gateState = GateState.CLOSED;
    }

    public void lockGate() {
        this.gateState = GateState.LOCKED;
    }

    public void handleAccesses(SystemState state) {
        switch (state) {
            case NORMAL -> closeGate();
            case AT_CAPACITY -> closeGate();
            case EMERGENCY -> lockGate();
            default -> closeGate();
        }
    }

    public boolean isGateOpen() {
        return gateState == GateState.OPEN;
    }

    public GateState getGateState() {
        return gateState;
    }
}
