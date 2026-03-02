package psms.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import psms.SystemState;

/**
 * Main JavaFX Application class for the Parking Structure Management System.
 */
public class PSMSApplication extends Application {
    private MainController controller;
    private Label systemStateLabel;
    private ParkingStructureView parkingView;
    private ControlPanel controlPanel;
    private StatusBar statusBar;
    private EventLogPanel eventLogPanel;

    @Override
    public void start(Stage primaryStage) {
        controller = new MainController();
        controller.setUiRefreshCallback(this::refreshUI);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #2b2b2b;");

        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Left: Event Log (fills full height)
        eventLogPanel = new EventLogPanel(controller);

        // Center: Parking view in a scroll pane
        parkingView = new ParkingStructureView(controller);
        ScrollPane parkingScroll = new ScrollPane(parkingView);
        parkingScroll.setFitToWidth(true);
        parkingScroll.setStyle("-fx-background: #2b2b2b; -fx-background-color: #2b2b2b; -fx-border-color: transparent;");
        HBox.setHgrow(parkingScroll, Priority.ALWAYS);

        // Right: Control Panel
        controlPanel = new ControlPanel(controller, this::refreshUI);

        HBox centerRow = new HBox(10, eventLogPanel, parkingScroll, controlPanel);
        centerRow.setPadding(new Insets(10, 0, 10, 0));
        VBox.setVgrow(centerRow, Priority.ALWAYS);

        root.setCenter(centerRow);

        statusBar = new StatusBar(controller);
        root.setBottom(statusBar);
        BorderPane.setMargin(statusBar, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(root, 1250, 700);
        primaryStage.setTitle("Parking Structure Management System - Demo");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        refreshUI();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");

        Label titleLabel = new Label("Parking Structure Management System");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: white;");

        systemStateLabel = new Label("System State: STARTUP");
        systemStateLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        systemStateLabel.setStyle("-fx-text-fill: #ffc66d; -fx-padding: 5 10; -fx-background-color: #4a4a4a; -fx-background-radius: 3;");

        HBox spacer = new HBox();
        spacer.setMinWidth(50);
        spacer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleLabel, spacer, systemStateLabel);
        return topBar;
    }

    public void refreshUI() {
        SystemState state = controller.getSystemState();
        systemStateLabel.setText("System State: " + state.name());

        String stateColor = switch (state) {
            case STARTUP -> "-fx-text-fill: #ffc66d;";
            case NORMAL -> "-fx-text-fill: #6aab73;";
            case AT_CAPACITY -> "-fx-text-fill: #e06c75;";
            case EMERGENCY -> "-fx-text-fill: #ff0000; -fx-font-weight: bold;";
        };
        systemStateLabel.setStyle(stateColor + " -fx-padding: 5 10; -fx-background-color: #4a4a4a; -fx-background-radius: 3;");

        parkingView.refresh();
        controlPanel.refresh();
        statusBar.refresh();
        eventLogPanel.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
