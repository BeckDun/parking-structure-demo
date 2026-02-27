package psms.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import psms.model.ParkingFloor;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual representation of the entire multi-floor parking structure.
 * Includes entrance display and all floor views.
 */
public class ParkingStructureView extends VBox {
    private final MainController controller;
    private final Label entranceDisplay;
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
        entranceDisplay.setStyle("-fx-text-fill: #6aab73;");

        entranceBox.getChildren().addAll(entranceTitle, entranceDisplay);

        getChildren().add(entranceBox);

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
            entranceDisplay.setStyle("-fx-text-fill: #e06c75;");
        } else {
            entranceDisplay.setStyle("-fx-text-fill: #6aab73;");
        }

        for (FloorView floorView : floorViews) {
            floorView.refresh();
        }
    }
}
