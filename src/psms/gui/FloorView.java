package psms.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import psms.model.ParkingFloor;
import psms.model.ParkingSpot;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual representation of a single floor in the parking structure.
 * Shows floor header with availability and a grid of parking spots.
 */
public class FloorView extends VBox {
    private final ParkingFloor floor;
    private final Label headerLabel;
    private final List<SpotView> spotViews;

    public FloorView(ParkingFloor floor) {
        this.floor = floor;
        this.spotViews = new ArrayList<>();

        setSpacing(8);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 8;");
        setAlignment(Pos.TOP_CENTER);

        headerLabel = new Label();
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        headerLabel.setStyle("-fx-text-fill: white;");

        FlowPane spotsPane = new FlowPane();
        spotsPane.setHgap(5);
        spotsPane.setVgap(5);
        spotsPane.setAlignment(Pos.CENTER);
        spotsPane.setPrefWrapLength(350);

        for (ParkingSpot spot : floor.getSpots()) {
            SpotView spotView = new SpotView(spot);
            spotViews.add(spotView);
            spotsPane.getChildren().add(spotView);
        }

        getChildren().addAll(headerLabel, spotsPane);
        refresh();
    }

    public void refresh() {
        int available = floor.getAvailableSpots();
        int total = floor.getTotalCapacity();
        headerLabel.setText("Floor " + floor.getFloorNumber() + " — " + available + " / " + total + " available");

        for (SpotView spotView : spotViews) {
            spotView.refresh();
        }
    }
}
