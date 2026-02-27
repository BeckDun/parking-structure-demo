package psms.gui;

import psms.*;
import psms.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller connecting the GUI to the PSMS logic.
 */
public class MainController {
    private static final int NUM_FLOORS = 3;
    private static final int SPOTS_PER_FLOOR = 30;
    private static final int HANDICAP_SPOTS_PER_FLOOR = 3;

    private final SystemController systemController;
    private final OccupancyManager occupancyManager;
    private final EntryGateController entryGateController;
    private final EmergencyHandler emergencyHandler;
    private final DisplayManager displayManager;
    private final List<ParkingFloor> floors;
    private final Random random;

    public MainController() {
        this.systemController = new SystemController();
        this.occupancyManager = new OccupancyManager();
        this.entryGateController = new EntryGateController();
        this.emergencyHandler = new EmergencyHandler(systemController);
        this.displayManager = new DisplayManager();
        this.floors = new ArrayList<>();
        this.random = new Random();
        initializeStructure();
    }

    private void initializeStructure() {
        systemController.transitionState(SystemState.STARTUP);
        entryGateController.lockGate();

        for (int i = 0; i < NUM_FLOORS; i++) {
            floors.add(new ParkingFloor(i + 1, SPOTS_PER_FLOOR, HANDICAP_SPOTS_PER_FLOOR));
        }

        synchronizeWithSensors();

        entryGateController.closeGate();
        systemController.transitionState(SystemState.NORMAL);
        updateEntranceDisplay();
    }

    public void simulateVehicleEntry() {
        if (systemController.getCurrentState() == SystemState.EMERGENCY) return;
        if (getTotalAvailableSpots() <= 0) {
            systemController.transitionState(SystemState.AT_CAPACITY);
            return;
        }

        entryGateController.openGate();
        occupancyManager.updateCount(true, false);

        ParkingSpot spot = findRandomSpot(false);
        if (spot != null) spot.setOccupied(true);

        entryGateController.closeGate();
        updateSystemState();
        updateEntranceDisplay();
    }

    public void simulateVehicleExit() {
        if (getOccupiedSpots() == 0) return;

        ParkingSpot spot = findRandomSpot(true);
        if (spot != null) {
            spot.setOccupied(false);
            occupancyManager.updateCount(false, spot.isHandicap());
        }

        updateSystemState();
        updateEntranceDisplay();
    }

    /**
     * Emergency State Activation (SAD 4.6):
     * 1. Emergency Handler triggers emergency
     * 2. Entry gate moves to LOCKED position
     * 3. Display shows emergency message
     */
    public void triggerEmergency() {
        emergencyHandler.triggerEmergency();
        entryGateController.lockGate();
        updateEntranceDisplay();
    }

    /**
     * Emergency State Resolution (SAD 4.7):
     * 1. Emergency Handler resolves emergency
     * 2. System initiates the Startup sequence (goes through STARTUP state)
     */
    public void resolveEmergency() {
        emergencyHandler.resolveEmergency();
        performStartupSequence();
    }

    /**
     * System Startup sequence (SAD 4.1):
     * 1. Transition to STARTUP state
     * 2. Synchronize occupancy counts with physical sensor network
     * 3. Entry gate in closed position
     * 4. Transition to NORMAL operation
     */
    private void performStartupSequence() {
        systemController.transitionState(SystemState.STARTUP);
        synchronizeWithSensors();
        entryGateController.closeGate();
        systemController.transitionState(SystemState.NORMAL);
        updateSystemState();
        updateEntranceDisplay();
    }

    /**
     * System Startup sequence (SAD 4.1):
     * 1. Transition to STARTUP state and lock entry gate
     * 2. Clear all spots (for demo - simulates empty structure)
     * 3. Synchronize occupancy counts with physical sensor network
     * 4. Activate all displays to reflect current occupancy
     * 5. Transition to NORMAL operation
     */
    public void resetSystem() {
        systemController.transitionState(SystemState.STARTUP);
        entryGateController.lockGate();

        clearAllSpots();
        synchronizeWithSensors();

        entryGateController.closeGate();
        systemController.transitionState(SystemState.NORMAL);
        updateEntranceDisplay();
    }

    private void clearAllSpots() {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                spot.setOccupied(false);
            }
        }
    }

    private void synchronizeWithSensors() {
        occupancyManager.reset(getTotalCapacity());
        int occupiedCount = 0;
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isOccupied()) {
                    occupiedCount++;
                }
            }
        }
        occupancyManager.setCurrentCount(occupiedCount);
    }

    private void updateSystemState() {
        if (systemController.getCurrentState() == SystemState.EMERGENCY) return;

        if (getTotalAvailableSpots() == 0) {
            systemController.transitionState(SystemState.AT_CAPACITY);
        } else if (systemController.getCurrentState() == SystemState.AT_CAPACITY) {
            systemController.transitionState(SystemState.NORMAL);
        }
    }

    private void updateEntranceDisplay() {
        int available = getTotalAvailableSpots();
        if (systemController.getCurrentState() == SystemState.EMERGENCY) {
            displayManager.setEntranceSign("CLOSED - EMERGENCY");
        } else if (available == 0) {
            displayManager.setEntranceSign("FULL");
        } else {
            displayManager.setEntranceSign(available + " SPOTS AVAILABLE");
        }
    }

    private ParkingSpot findRandomSpot(boolean occupied) {
        List<ParkingSpot> candidates = new ArrayList<>();
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isOccupied() == occupied) candidates.add(spot);
            }
        }
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    public int getTotalAvailableSpots() {
        return floors.stream().mapToInt(ParkingFloor::getAvailableSpots).sum();
    }

    public int getOccupiedSpots() {
        return floors.stream().mapToInt(ParkingFloor::getOccupiedSpots).sum();
    }

    public int getTotalCapacity() {
        return NUM_FLOORS * SPOTS_PER_FLOOR;
    }

    public List<ParkingFloor> getFloors() {
        return floors;
    }

    public SystemState getSystemState() {
        return systemController.getCurrentState();
    }

    public GateState getGateState() {
        return entryGateController.getGateState();
    }

    public String getEntranceMessage() {
        return displayManager.getEntranceMessage();
    }
}
