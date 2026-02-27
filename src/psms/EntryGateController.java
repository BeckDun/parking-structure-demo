package psms;

import psms.model.GateState;

/**
 * Entry Gate Controller - Receives open/close commands and controls the physical entry gate.
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

    public GateState getGateState() {
        return gateState;
    }
}
