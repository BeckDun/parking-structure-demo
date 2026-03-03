package psms.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import psms.*;
import psms.display.EntranceDisplay;
import psms.drivers.DisplayDriver;
import psms.drivers.SensorDriver;
import psms.model.*;
import psms.sensors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller connecting the GUI to the PSMS logic.
 * All sensor-driven operations follow the SAD architecture data flow:
 *
 *   Input:  Sensor → SensorDriver → SensorInterface → SystemController → Subsystems
 *   Output: SystemController → DisplayManager → DisplayDriver → Display Devices
 *
 * MainController only interacts with SensorDriver (input side) and
 * SystemController (command side). It never touches SensorInterface directly.
 */
public class MainController {
    private static final int NUM_FLOORS = 3;
    private static final int SPOTS_PER_FLOOR = 30;
    private static final int HANDICAP_SPOTS_PER_FLOOR = 3;

    // SAD Section 3 - Internal Software Components
    private final SystemController systemController;
    private final OccupancyManager occupancyManager;
    private final EntryGateController entryGateController;
    private final EmergencyHandler emergencyHandler;
    private final DisplayManager displayManager;

    // SAD Section 2.3 - Drivers (SensorDriver talks to SensorInterface; DisplayDriver talks to Display Devices)
    private final SensorDriver sensorDriver;

    // SAD Section 2.3 - External Input Devices
    private final EntryGateSensor entryGateSensor;
    private final ExitGateSensor exitGateSensor;
    private final SpotSensor spotSensor;
    private final ManualOverrideSwitch manualOverrideSwitch;

    private final List<ParkingFloor> floors;
    private final Random random;
    private final List<String> eventLog;

    private Runnable uiRefreshCallback;
    private boolean gateInUse;

    public MainController() {
        // 1. Create external output device
        EntranceDisplay entranceDisplay = new EntranceDisplay();

        // 2. Create Display Driver → wired to external output devices
        DisplayDriver displayDriver = new DisplayDriver(entranceDisplay);

        // 3. Create Display Manager → talks only to Display Driver
        this.displayManager = new DisplayManager(displayDriver);

        // 4. Create internal subsystem components
        this.systemController = new SystemController();
        this.occupancyManager = new OccupancyManager();
        this.entryGateController = new EntryGateController();
        this.emergencyHandler = new EmergencyHandler(systemController);

        // 5. Wire System Controller → commands subsystems (EntryGateController, OccupancyManager, DisplayManager)
        systemController.setSubsystems(entryGateController, occupancyManager, displayManager);

        // 6. Create Sensor Interface → talks only to System Controller
        SensorInterface sensorInterface = new SensorInterface(systemController);

        // 7. Create Sensor Driver → talks only to Sensor Interface
        this.sensorDriver = new SensorDriver(sensorInterface);

        // 8. Create external input devices
        this.entryGateSensor = new EntryGateSensor();
        this.exitGateSensor = new ExitGateSensor();
        this.spotSensor = new SpotSensor();
        this.manualOverrideSwitch = new ManualOverrideSwitch();

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

    // ---- System Startup (SAD 4.1) ----

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
        systemController.refreshDisplay();
    }

    // ---- Vehicle Entry (SAD 4.2) ----

    /**
     * Animated multi-step entry sequence following the SAD data flow:
     * 1. EntryGateSensor triggers → SensorDriver.processSensorReading()
     *    → (internally: SensorDriver → SensorInterface → SystemController)
     *    → SystemController opens gate, updates OccupancyManager and DisplayManager
     * 2. Timed: gate closes, then SpotSensor triggers the same chain for parking
     */
    public void simulateVehicleEntry() {
        if (systemController.getCurrentState() == SystemState.EMERGENCY) return;

        // External Input Device - Entry Gate Sensor triggered
        entryGateSensor.setVehicleDetected(true);
        log("[SENSOR] Entry gate sensor triggered - vehicle detected");

        // SensorDriver reads sensor → forwards to SensorInterface → routes to SystemController
        boolean allowed = sensorDriver.processSensorReading("ENTRY_GATE", entryGateSensor.isVehicleDetected());

        entryGateSensor.setVehicleDetected(false);

        if (!allowed) {
            log("[SYSTEM] Structure at capacity - entry denied");
            refreshUI();
            return;
        }

        log("[GATE] Entry gate OPEN");
        log("[SYSTEM] Vehicle entered - now In-Transit");
        gateInUse = true;
        refreshUI();

        Timeline timeline = new Timeline();

        // Gate closes after 1 second (System Controller command to Entry Gate Controller)
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e -> {
            systemController.processEvent("GATE_CLOSE");
            gateInUse = false;
            log("[GATE] Entry gate CLOSED");
            refreshUI();
        }));

        // Vehicle parks after 2.5 seconds - Spot Sensor triggers the full chain
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2500), e -> {
            ParkingSpot spot = findRandomSpot(false);
            if (spot != null) {
                spot.setOccupied(true);
                log("[SENSOR] Spot sensor triggered - vehicle parked at Floor "
                        + spot.getFloorNumber() + ", Spot " + (spot.getSpotId() + 1));

                // SpotSensor → SensorDriver → SensorInterface → SystemController
                spotSensor.setOccupied(true);
                sensorDriver.processSensorReading("SPOT_SENSOR", spotSensor.isOccupied());
            }
            refreshUI();
        }));

        timeline.play();
    }

    // ---- Vehicle Exit (SAD 4.4) ----

    /**
     * Animated multi-step exit sequence following the SAD data flow:
     * 1. SpotSensor triggers vacancy → SensorDriver → SensorInterface → SystemController
     * 2. Timed: ExitGateSensor triggers → same chain → SystemController
     */
    public void simulateVehicleExit() {
        if (getOccupiedSpots() == 0) return;

        ParkingSpot spot = findRandomSpot(true);
        if (spot == null) return;

        // External Input Device - Spot Sensor detects vehicle leaving
        spot.setOccupied(false);
        spotSensor.setOccupied(false);
        log("[SENSOR] Spot sensor triggered - vehicle left Floor "
                + spot.getFloorNumber() + ", Spot " + (spot.getSpotId() + 1));

        // SpotSensor → SensorDriver → SensorInterface → SystemController ("SPOT_VACANT")
        sensorDriver.processSensorReading("SPOT_SENSOR", spotSensor.isOccupied());

        log("[SYSTEM] Vehicle In-Transit toward exit");
        refreshUI();

        // Vehicle reaches exit after 2 seconds
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2000), e -> {
            // ExitGateSensor → SensorDriver → SensorInterface → SystemController ("VEHICLE_EXIT")
            exitGateSensor.setVehicleDetected(true);
            log("[SENSOR] Exit gate sensor triggered - vehicle has left the structure");

            sensorDriver.processSensorReading("EXIT_GATE", exitGateSensor.isVehicleDetected());

            exitGateSensor.setVehicleDetected(false);
            refreshUI();
        }));
        timeline.play();
    }

    // ---- Emergency (SAD 4.6 / 4.7) ----

    /**
     * Emergency activation: Manual Override Switch triggers Emergency Handler,
     * which signals System Controller via processEvent("EMERGENCY_TRIGGER").
     * System Controller locks gate and updates display.
     */
    public void triggerEmergency() {
        log("[EMERGENCY] Emergency triggered!");

        // External Input Device - Manual Override Switch activated
        manualOverrideSwitch.setActivated(true);

        // Emergency Handler monitors switch and signals System Controller
        emergencyHandler.setManualOverride(true);
        emergencyHandler.monitorInputs();

        log("[GATE] Entry gate LOCKED");
        log("[SYSTEM] Transitioned to EMERGENCY state");
        gateInUse = false;
    }

    /**
     * Emergency resolution: Emergency Handler resolves and signals
     * System Controller, then the Startup sequence is performed.
     */
    public void resolveEmergency() {
        log("[EMERGENCY] Emergency resolved");
        manualOverrideSwitch.setActivated(false);
        emergencyHandler.resolveEmergency();
        performStartupSequence();
    }

    // ---- Startup / Reset ----

    private void performStartupSequence() {
        log("[SYSTEM] Initiating Startup sequence");
        systemController.transitionState(SystemState.STARTUP);
        synchronizeWithSensors();
        log("[SENSOR] Synchronized occupancy with sensor network");
        entryGateController.closeGate();
        log("[GATE] Entry gate CLOSED");
        systemController.transitionState(SystemState.NORMAL);
        log("[SYSTEM] Transitioned to NORMAL operation");
        systemController.refreshDisplay();
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
        systemController.refreshDisplay();
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
        entryGateController.lockGate();
        log("[SYSTEM] Transitioned to AT_CAPACITY");
        systemController.refreshDisplay();
    }

    // ---- Internal helpers ----

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

    private ParkingSpot findRandomSpot(boolean occupied) {
        List<ParkingSpot> candidates = new ArrayList<>();
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isOccupied() == occupied) candidates.add(spot);
            }
        }
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    // ---- Public getters for GUI ----

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
