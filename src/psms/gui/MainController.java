package psms.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import psms.*;
import psms.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller connecting the GUI to the PSMS logic.
 * Vehicle entry is animated to show the gate/in-transit/parking sequence.
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
    private final List<String> eventLog;

    private Runnable uiRefreshCallback;
    private boolean gateInUse;

    public MainController() {
        this.systemController = new SystemController();
        this.occupancyManager = new OccupancyManager();
        this.entryGateController = new EntryGateController();
        this.emergencyHandler = new EmergencyHandler(systemController);
        this.displayManager = new DisplayManager();
        this.floors = new ArrayList<>();
        this.random = new Random();
        this.eventLog = new ArrayList<>();
        this.gateInUse = false;
        initializeStructure();
    }

    public void setUiRefreshCallback(Runnable callback) {
        this.uiRefreshCallback = callback;
    }

    private void refreshUI() {
        if (uiRefreshCallback != null) uiRefreshCallback.run();
    }

    private void log(String message) {
        eventLog.add(message);
    }

    public List<String> getEventLog() {
        return eventLog;
    }

    private void initializeStructure() {
        log("[SYSTEM] Entering STARTUP state");
        systemController.transitionState(SystemState.STARTUP);
        entryGateController.lockGate();
        log("[GATE] Entry gate LOCKED");

        for (int i = 0; i < NUM_FLOORS; i++) {
            floors.add(new ParkingFloor(i + 1, SPOTS_PER_FLOOR, HANDICAP_SPOTS_PER_FLOOR));
        }
        log("[SYSTEM] Initialized " + NUM_FLOORS + " floors, " + SPOTS_PER_FLOOR + " spots each");

        synchronizeWithSensors();
        log("[SENSOR] Synchronized occupancy with sensor network");

        entryGateController.closeGate();
        log("[GATE] Entry gate CLOSED");
        systemController.transitionState(SystemState.NORMAL);
        log("[SYSTEM] Transitioned to NORMAL operation");
        updateEntranceDisplay();
    }

    /**
     * Vehicle Entry (SAD 4.2) - animated multi-step sequence:
     * 1. Check capacity → open gate
     * 2. Vehicle enters → in-transit count increments, gate closes
     * 3. Vehicle finds spot → parks, in-transit decrements
     */
    public void simulateVehicleEntry() {
        if (systemController.getCurrentState() == SystemState.EMERGENCY) return;
        if (getTotalAvailableSpots() <= 0) {
            log("[SENSOR] Entry gate sensor triggered - vehicle detected");
            log("[SYSTEM] Structure at capacity - entry denied");
            systemController.transitionState(SystemState.AT_CAPACITY);
            refreshUI();
            return;
        }

        log("[SENSOR] Entry gate sensor triggered - vehicle detected");
        log("[GATE] Entry gate OPEN");
        gateInUse = true;
        entryGateController.openGate();
        occupancyManager.vehicleEntered();
        log("[SYSTEM] Vehicle entered - now In-Transit");
        updateSystemState();
        updateEntranceDisplay();
        refreshUI();

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e -> {
            entryGateController.closeGate();
            gateInUse = false;
            log("[GATE] Entry gate CLOSED");
            refreshUI();
        }));

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2500), e -> {
            ParkingSpot spot = findRandomSpot(false);
            if (spot != null) {
                spot.setOccupied(true);
                log("[SENSOR] Spot sensor triggered - vehicle parked at Floor "
                        + spot.getFloorNumber() + ", Spot " + (spot.getSpotId() + 1));
            }
            occupancyManager.vehicleParked();
            updateSystemState();
            updateEntranceDisplay();
            refreshUI();
        }));

        timeline.play();
    }

    /**
     * Vehicle Exit (SAD 4.4) - animated multi-step sequence:
     * 1. Vehicle vacates stall → spot frees, vehicle is in-transit
     * 2. Vehicle drives to exit and passes exit sensor → exits structure
     */
    public void simulateVehicleExit() {
        if (getOccupiedSpots() == 0) return;

        ParkingSpot spot = findRandomSpot(true);
        if (spot == null) return;

        log("[SENSOR] Spot sensor triggered - vehicle left Floor "
                + spot.getFloorNumber() + ", Spot " + (spot.getSpotId() + 1));
        spot.setOccupied(false);
        occupancyManager.vehicleUnparked();
        log("[SYSTEM] Vehicle In-Transit toward exit");
        updateSystemState();
        updateEntranceDisplay();
        refreshUI();

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2000), e -> {
            log("[SENSOR] Exit gate sensor triggered - vehicle has left the structure");
            occupancyManager.vehicleExited();
            updateSystemState();
            updateEntranceDisplay();
            refreshUI();
        }));
        timeline.play();
    }

    /**
     * Emergency State Activation (SAD 4.6):
     * 1. Emergency Handler triggers emergency
     * 2. Entry gate moves to LOCKED position
     * 3. Display shows emergency message
     */
    public void triggerEmergency() {
        log("[EMERGENCY] Emergency triggered!");
        emergencyHandler.triggerEmergency();
        entryGateController.lockGate();
        log("[GATE] Entry gate LOCKED");
        log("[SYSTEM] Transitioned to EMERGENCY state");
        gateInUse = false;
        updateEntranceDisplay();
    }

    /**
     * Emergency State Resolution (SAD 4.7):
     * 1. Emergency Handler resolves emergency
     * 2. System initiates the Startup sequence (goes through STARTUP state)
     */
    public void resolveEmergency() {
        log("[EMERGENCY] Emergency resolved");
        emergencyHandler.resolveEmergency();
        performStartupSequence();
    }

    private void performStartupSequence() {
        log("[SYSTEM] Initiating Startup sequence");
        systemController.transitionState(SystemState.STARTUP);
        synchronizeWithSensors();
        log("[SENSOR] Synchronized occupancy with sensor network");
        entryGateController.closeGate();
        log("[GATE] Entry gate CLOSED");
        systemController.transitionState(SystemState.NORMAL);
        log("[SYSTEM] Transitioned to NORMAL operation");
        updateSystemState();
        updateEntranceDisplay();
    }

    public void resetSystem() {
        log("[SYSTEM] System reset initiated");
        systemController.transitionState(SystemState.STARTUP);
        log("[SYSTEM] Entering STARTUP state");
        entryGateController.lockGate();
        log("[GATE] Entry gate LOCKED");
        gateInUse = false;

        clearAllSpots();
        synchronizeWithSensors();
        log("[SENSOR] Synchronized occupancy with sensor network");

        entryGateController.closeGate();
        log("[GATE] Entry gate CLOSED");
        systemController.transitionState(SystemState.NORMAL);
        log("[SYSTEM] Transitioned to NORMAL operation");
        updateEntranceDisplay();
    }

    public void fillAllSpots() {
        if (systemController.getCurrentState() == SystemState.EMERGENCY) return;

        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                spot.setOccupied(true);
            }
        }
        synchronizeWithSensors();
        log("[SYSTEM] All spots filled (demo)");
        systemController.transitionState(SystemState.AT_CAPACITY);
        entryGateController.closeGate();
        log("[SYSTEM] Transitioned to AT_CAPACITY");
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

    public int getInTransitCount() {
        return occupancyManager.getInTransitCount();
    }

    public int getParkedCount() {
        return occupancyManager.getParkedCount();
    }

    public boolean isGateInUse() {
        return gateInUse;
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
