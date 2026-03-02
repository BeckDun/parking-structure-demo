# Parking Structure Management System (PSMS)

A JavaFX demo application for a multi-floor parking structure management system, developed for CS 460 Software Engineering at UNM.

## Team T01
- Beckett Dunlavy (Manager)
- Aditya Chauhan
- Christian Maestas
- Oscar McCoy
- Isaac Tapia

## Requirements
- Java 17 or higher
- Maven 3.6+

## Run Instructions

### Option 1: Using Maven (Recommended)
```bash
mvn clean javafx:run
```

### Option 2: Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Let Maven import dependencies (click "Load Maven Project" if prompted)
3. Run `Demo.java` located in `src/demo/`

### Option 3: Command Line with Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="demo.Demo"
```

## Features

The demo simulates all documented use cases from the SAD:

- **Vehicle Entry** - Simulates vehicles entering the structure
- **Vehicle Exit** - Simulates vehicles leaving (works during emergency per SRS)
- **Emergency Mode** - Locks entry gate, displays emergency message
- **Emergency Resolution** - Goes through startup sequence to resume normal operation
- **System Reset** - Reinitializes system and synchronizes with sensors

## System States

| State | Description |
|-------|-------------|
| STARTUP | System initializing, synchronizing with sensors |
| NORMAL | Standard operation, vehicles may enter/exit |
| AT_CAPACITY | Structure full, entry blocked until vehicle exits |
| EMERGENCY | Entry locked, vehicles may exit safely |

## Project Structure

```
src/
├── demo/Demo.java              # Entry point
├── module-info.java            # Java module configuration
└── psms/
    ├── SystemController.java   # Central state machine
    ├── EntryGateController.java
    ├── OccupancyManager.java
    ├── EmergencyHandler.java
    ├── DisplayManager.java
    ├── model/                  # Data classes
    ├── sensors/                # Sensor representations
    ├── drivers/                # Hardware driver stubs
    ├── display/                # Display hardware
    └── gui/                    # JavaFX interface
```

## Documentation

Requirements/Design documents are located in `resources/`:
- T01-RDD.pdf - Requirements Definition Document
- T01-SRS.pdf - Software Requirements Specification
- T01-SAD.pdf - Software Architecture Design
