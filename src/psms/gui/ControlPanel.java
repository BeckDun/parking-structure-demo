package psms.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import psms.SystemState;

/**
 * Control panel with simulation buttons for the PSMS demo.
 */
public class ControlPanel extends VBox {
    private final MainController controller;
    private final Runnable refreshCallback;

    private final Button entryButton;
    private final Button exitButton;
    private final Button emergencyButton;
    private final Button resolveButton;
    private final Button resetButton;
    private final Button fillButton;

    private final Label occupiedLabel;
    private final Label availableLabel;
    private final Label capacityLabel;

    public ControlPanel(MainController controller, Runnable refreshCallback) {
        this.controller = controller;
        this.refreshCallback = refreshCallback;

        setSpacing(12);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(220);
        setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 8;");

        // Title
        Label title = new Label("Simulation Controls");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: white;");

        // Statistics Section
        Label statsTitle = new Label("Statistics");
        statsTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        statsTitle.setStyle("-fx-text-fill: #888888;");

        capacityLabel = createStatLabel("Total Capacity: 90");
        occupiedLabel = createStatLabel("Occupied: 0");
        availableLabel = createStatLabel("Available: 90");

        VBox statsBox = new VBox(5, statsTitle, capacityLabel, occupiedLabel, availableLabel);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 5;");

        // Vehicle Controls
        Label vehicleTitle = new Label("Vehicle Simulation");
        vehicleTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        vehicleTitle.setStyle("-fx-text-fill: #888888;");

        entryButton = createButton("Vehicle Entry", "#7CFC00");
        entryButton.setOnAction(e -> {
            controller.simulateVehicleEntry();
            refreshCallback.run();
        });

        exitButton = createButton("Vehicle Exit", "#61afef");
        exitButton.setOnAction(e -> {
            controller.simulateVehicleExit();
            refreshCallback.run();
        });

        VBox vehicleBox = new VBox(8, vehicleTitle, entryButton, exitButton);
        vehicleBox.setPadding(new Insets(10));
        vehicleBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 5;");

        // Emergency Controls
        Label emergencyTitle = new Label("Emergency Controls");
        emergencyTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        emergencyTitle.setStyle("-fx-text-fill: #888888;");

        emergencyButton = createButton("Trigger Emergency", "#FF3F3D");
        emergencyButton.setOnAction(e -> {
            controller.triggerEmergency();
            refreshCallback.run();
        });

        resolveButton = createButton("Resolve Emergency", "#ffc66d");
        resolveButton.setOnAction(e -> {
            controller.resolveEmergency();
            refreshCallback.run();
        });

        VBox emergencyBox = new VBox(8, emergencyTitle, emergencyButton, resolveButton);
        emergencyBox.setPadding(new Insets(10));
        emergencyBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 5;");

        // System Controls
        Label systemTitle = new Label("System Controls");
        systemTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        systemTitle.setStyle("-fx-text-fill: #888888;");

        fillButton = createButton("Fill All Spots", "#c678dd");
        fillButton.setOnAction(e -> {
            controller.fillAllSpots();
            refreshCallback.run();
        });

        resetButton = createButton("Reset System", "#888888");
        resetButton.setOnAction(e -> {
            controller.resetSystem();
            refreshCallback.run();
        });

        VBox systemBox = new VBox(8, systemTitle, fillButton, resetButton);
        systemBox.setPadding(new Insets(10));
        systemBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 5;");

        // Legend
        Label legendTitle = new Label("Spot Legend");
        legendTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        legendTitle.setStyle("-fx-text-fill: #888888;");

        Label greenLegend = createStatLabel("Green = Available");
        greenLegend.setStyle("-fx-text-fill: #7CFC00;");
        Label redLegend = createStatLabel("Red = Occupied");
        redLegend.setStyle("-fx-text-fill: #FF3F3D;");
        Label blueLegend = createStatLabel("Blue = Handicap Available");
        blueLegend.setStyle("-fx-text-fill: #61afef;");

        VBox legendBox = new VBox(5, legendTitle, greenLegend, redLegend, blueLegend);
        legendBox.setPadding(new Insets(10));
        legendBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 5;");

        getChildren().addAll(
                title,
                new Separator(),
                statsBox,
                vehicleBox,
                emergencyBox,
                systemBox,
                legendBox
        );

        refresh();
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + color + ", -20%);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        ));
        return button;
    }

    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", 12));
        label.setStyle("-fx-text-fill: white;");
        return label;
    }

    public void refresh() {
        int occupied = controller.getOccupiedSpots();
        int available = controller.getTotalAvailableSpots();
        int capacity = controller.getTotalCapacity();

        capacityLabel.setText("Total Capacity: " + capacity);
        occupiedLabel.setText("Occupied: " + occupied);
        availableLabel.setText("Available: " + available);
        occupiedLabel.setStyle("-fx-text-fill: white;");

        SystemState state = controller.getSystemState();
        boolean isEmergency = state == SystemState.EMERGENCY;
        boolean gateInUse = controller.isGateInUse();

        entryButton.setDisable(isEmergency || state == SystemState.AT_CAPACITY || gateInUse);
        exitButton.setDisable(occupied == 0);
        emergencyButton.setDisable(isEmergency);
        resolveButton.setDisable(!isEmergency);
        fillButton.setDisable(isEmergency || available == 0);
    }
}
