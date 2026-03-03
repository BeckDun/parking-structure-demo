package psms.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Scrollable event log showing sensor triggers, gate actions, and state changes.
 */
public class EventLogPanel extends VBox {
    private final MainController controller;
    private final VBox logContent;
    private final ScrollPane scrollPane;
    private int lastSize;

    public EventLogPanel(MainController controller) {
        this.controller = controller;
        this.lastSize = 0;

        setMinWidth(270);
        setPrefWidth(270);
        setMaxHeight(Double.MAX_VALUE);
        setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 8;");
        setPadding(new Insets(10));
        setSpacing(8);

        Label title = new Label("Event Log");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: white;");

        logContent = new VBox(3);
        logContent.setPadding(new Insets(8));
        logContent.setStyle("-fx-background-color: #1a1a1a;");

        scrollPane = new ScrollPane(logContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a1a; -fx-background-color: #1a1a1a; -fx-border-color: transparent;");
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(title, scrollPane);
        refresh();
    }

    public void refresh() {
        List<String> log = controller.getEventLog();
        if (log.size() == lastSize) return;

        for (int i = lastSize; i < log.size(); i++) {
            String entry = log.get(i);
            Label label = new Label(entry);
            label.setFont(Font.font("Monospaced", 11));
            label.setWrapText(true);
            label.setMaxWidth(240);

            if (entry.startsWith("[EMERGENCY]")) {
                label.setStyle("-fx-text-fill: #ff4444;");
            } else if (entry.startsWith("[GATE]")) {
                label.setStyle("-fx-text-fill: #61afef;");
            } else if (entry.startsWith("[SENSOR]")) {
                label.setStyle("-fx-text-fill: #ffc66d;");
            } else if (entry.startsWith("[SYSTEM]")) {
                label.setStyle("-fx-text-fill: #7CFC00;");
            } else {
                label.setStyle("-fx-text-fill: #888888;");
            }

            logContent.getChildren().add(label);
        }

        lastSize = log.size();
        scrollPane.setVvalue(1.0);
    }
}
