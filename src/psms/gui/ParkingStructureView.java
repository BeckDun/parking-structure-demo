package psms.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import psms.model.ParkingFloor;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual representation of the entire multi-floor parking structure.
 * Includes entrance display, in-transit monitor, and all floor views.
 */
public class ParkingStructureView extends VBox {
    private final MainController controller;
    private final Label entranceDisplay;
    private final Label inTransitValue;
    private final Label parkedValue;
    private final Label inTransitIndicator;
    private final List<FloorView> floorViews;

    public ParkingStructureView(MainController controller) {
        this.controller = controller;
        this.floorViews = new ArrayList<>();

        setSpacing(15);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #2b2b2b;");

        // Entrance Display
        VBox entranceBox = new VBox(5);
        entranceBox.setAlignment(Pos.CENTER);
        entranceBox.setPadding(new Insets(15));
        entranceBox.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 10; -fx-border-color: #ffc66d; -fx-border-width: 2; -fx-border-radius: 10;");

        Label entranceTitle = new Label("ENTRANCE DISPLAY");
        entranceTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        entranceTitle.setStyle("-fx-text-fill: #888888;");

        entranceDisplay = new Label();
        entranceDisplay.setFont(Font.font("System", FontWeight.BOLD, 24));
        entranceDisplay.setStyle("-fx-text-fill: #7CFC00;");

        entranceBox.getChildren().addAll(entranceTitle, entranceDisplay);

        // Vehicle Status Monitor
        HBox vehicleMonitor = new HBox(30);
        vehicleMonitor.setAlignment(Pos.CENTER);
        vehicleMonitor.setPadding(new Insets(12));
        vehicleMonitor.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 8;");

        inTransitIndicator = new Label();
        inTransitIndicator.setMinWidth(10);
        inTransitIndicator.setMinHeight(10);
        inTransitIndicator.setMaxWidth(10);
        inTransitIndicator.setMaxHeight(10);
        inTransitIndicator.setStyle("-fx-background-color: #555555; -fx-background-radius: 5;");

        Label inTransitLabel = new Label("In-Transit:");
        inTransitLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        inTransitLabel.setStyle("-fx-text-fill: #888888;");

        inTransitValue = new Label("0");
        inTransitValue.setFont(Font.font("System", FontWeight.BOLD, 20));
        inTransitValue.setStyle("-fx-text-fill: #888888;");

        HBox inTransitBox = new HBox(8, inTransitIndicator, inTransitLabel, inTransitValue);
        inTransitBox.setAlignment(Pos.CENTER);

        Label parkedLabel = new Label("Parked:");
        parkedLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        parkedLabel.setStyle("-fx-text-fill: #7CFC00;");

        parkedValue = new Label("0");
        parkedValue.setFont(Font.font("System", FontWeight.BOLD, 20));
        parkedValue.setStyle("-fx-text-fill: #7CFC00;");

        HBox parkedBox = new HBox(8, parkedLabel, parkedValue);
        parkedBox.setAlignment(Pos.CENTER);

        vehicleMonitor.getChildren().addAll(inTransitBox, parkedBox);

        getChildren().addAll(entranceBox, vehicleMonitor);

        // Floor Views (top floor first)
        List<ParkingFloor> floors = controller.getFloors();
        for (int i = floors.size() - 1; i >= 0; i--) {
            FloorView floorView = new FloorView(floors.get(i));
            floorViews.add(floorView);
            getChildren().add(floorView);
        }

        refresh();
    }

    public void refresh() {
        String message = controller.getEntranceMessage();
        entranceDisplay.setText(message);

        if (message.contains("FULL") || message.contains("EMERGENCY")) {
            entranceDisplay.setStyle("-fx-text-fill: #FF3F3D;");
        } else {
            entranceDisplay.setStyle("-fx-text-fill: #7CFC00;");
        }

        int inTransit = controller.getInTransitCount();
        int parked = controller.getParkedCount();

        inTransitValue.setText(String.valueOf(inTransit));
        parkedValue.setText(String.valueOf(parked));

        if (inTransit > 0) {
            inTransitValue.setStyle("-fx-text-fill: #ffc66d;");
            inTransitIndicator.setStyle("-fx-background-color: #ffc66d; -fx-background-radius: 5;");
        } else {
            inTransitValue.setStyle("-fx-text-fill: #888888;");
            inTransitIndicator.setStyle("-fx-background-color: #555555; -fx-background-radius: 5;");
        }

        for (FloorView floorView : floorViews) {
            floorView.refresh();
        }
    }
}
