package psms.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import psms.model.GateState;

/**
 * Bottom status bar showing gate state and summary statistics.
 */
public class StatusBar extends HBox {
    private final MainController controller;
    private final Label gateStatusLabel;
    private final Label gateIndicator;
    private final Label summaryLabel;

    public StatusBar(MainController controller) {
        this.controller = controller;

        setSpacing(20);
        setPadding(new Insets(12));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");

        // Gate Status
        HBox gateBox = new HBox(10);
        gateBox.setAlignment(Pos.CENTER_LEFT);

        Label gateLabel = new Label("Entry Gate:");
        gateLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        gateLabel.setStyle("-fx-text-fill: #888888;");

        gateIndicator = new Label();
        gateIndicator.setMinWidth(15);
        gateIndicator.setMinHeight(15);
        gateIndicator.setMaxWidth(15);
        gateIndicator.setMaxHeight(15);
        gateIndicator.setStyle("-fx-background-color: #e06c75; -fx-background-radius: 7;");

        gateStatusLabel = new Label("CLOSED");
        gateStatusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        gateStatusLabel.setStyle("-fx-text-fill: white;");

        gateBox.getChildren().addAll(gateLabel, gateIndicator, gateStatusLabel);

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Summary
        summaryLabel = new Label();
        summaryLabel.setFont(Font.font("System", 12));
        summaryLabel.setStyle("-fx-text-fill: #888888;");

        getChildren().addAll(gateBox, spacer, summaryLabel);
        refresh();
    }

    public void refresh() {
        GateState gateState = controller.getGateState();

        String stateText;
        String indicatorColor;

        switch (gateState) {
            case OPEN -> {
                stateText = "OPEN";
                indicatorColor = "#6aab73";
            }
            case CLOSED -> {
                stateText = "CLOSED";
                indicatorColor = "#e06c75";
            }
            case LOCKED -> {
                stateText = "LOCKED";
                indicatorColor = "#ff0000";
            }
            default -> {
                stateText = "UNKNOWN";
                indicatorColor = "#888888";
            }
        }

        gateStatusLabel.setText(stateText);
        gateIndicator.setStyle("-fx-background-color: " + indicatorColor + "; -fx-background-radius: 7;");

        int occupied = controller.getOccupiedSpots();
        int available = controller.getTotalAvailableSpots();
        int capacity = controller.getTotalCapacity();

        summaryLabel.setText(
                "Occupied: " + occupied + " | " +
                "Available: " + available + " | " +
                "Capacity: " + capacity
        );
    }
}
