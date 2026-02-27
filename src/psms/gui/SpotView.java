package psms.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import psms.model.ParkingSpot;
import psms.model.SpotStatus;

/**
 * Visual representation of an individual parking spot.
 * Displays as a colored rectangle: green (available), red (occupied), blue (handicap available).
 */
public class SpotView extends StackPane {
    private static final double SPOT_WIDTH = 35;
    private static final double SPOT_HEIGHT = 25;

    private final ParkingSpot spot;
    private final Rectangle rectangle;
    private final Text spotLabel;

    public SpotView(ParkingSpot spot) {
        this.spot = spot;

        rectangle = new Rectangle(SPOT_WIDTH, SPOT_HEIGHT);
        rectangle.setArcWidth(5);
        rectangle.setArcHeight(5);
        rectangle.setStroke(Color.web("#1a1a1a"));
        rectangle.setStrokeWidth(1);

        spotLabel = new Text(String.valueOf(spot.getSpotId() + 1));
        spotLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        spotLabel.setFill(Color.WHITE);

        getChildren().addAll(rectangle, spotLabel);
        refresh();
    }

    public void refresh() {
        SpotStatus status = spot.getStatus();
        Color color = switch (status) {
            case AVAILABLE -> Color.web("#6aab73");
            case OCCUPIED -> Color.web("#e06c75");
            case HANDICAP_AVAILABLE -> Color.web("#61afef");
        };
        rectangle.setFill(color);

        if (spot.isHandicap() && !spot.isOccupied()) {
            spotLabel.setText("H");
        } else {
            spotLabel.setText(String.valueOf(spot.getSpotId() + 1));
        }
    }
}
