package psms.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    @Override
    public void start(Stage primaryStage) {
        controller = new MainController();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #2b2b2b;");

        // Top - Title and System State
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Center - Parking Structure Visualization
        parkingView = new ParkingStructureView(controller);
        root.setCenter(parkingView);
        BorderPane.setMargin(parkingView, new Insets(10, 10, 10, 0));

        // Right - Control Panel
        controlPanel = new ControlPanel(controller, this::refreshUI);
        root.setRight(controlPanel);
        BorderPane.setMargin(controlPanel, new Insets(10, 0, 10, 10));

        // Bottom - Status Bar
        statusBar = new StatusBar(controller);
        root.setBottom(statusBar);
        BorderPane.setMargin(statusBar, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Parking Structure Management System - Demo");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
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
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
