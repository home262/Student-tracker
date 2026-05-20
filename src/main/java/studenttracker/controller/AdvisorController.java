package studenttracker.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import studenttracker.App;
import studenttracker.model.Student;
import studenttracker.service.AIAdvisorService;
import studenttracker.service.StudentService;

public class AdvisorController implements StudentAware {

    @FXML private VBox              chatBox;
    @FXML private ScrollPane        chatScrollPane;
    @FXML private TextField         inputField;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label             statusLabel;

    private Student            student;
    private final AIAdvisorService aiService = new AIAdvisorService();
    private double             gpa;
    private boolean            greeted = false;

    @FXML
    public void initialize() {
        if (loadingIndicator != null) loadingIndicator.setVisible(false);
        // Auto-scroll whenever the chatBox height changes
        chatBox.heightProperty().addListener((obs, oldVal, newVal) ->
            chatScrollPane.setVvalue(1.0));
    }

    @Override
    public void setStudent(Student student) {
        this.student = student;
        StudentService ss = new StudentService(App.getDatabase());
        this.gpa = ss.calculateGPA(student);

        if (!greeted) {
            greeted = true;
            String firstName = student.getName().split(" ")[0];
            addOracleMessage(
                "Greetings, Scholar " + firstName + "! ⚔\n\n" +
                "I am the Oracle of Scholar's Sanctum. I hold your full academic record — " +
                "your grades, your presence rate, and your standing within these halls.\n\n" +
                "You may ask me anything about your studies, or press **Summon Wisdom** " +
                "for a complete analysis of your current path.\n\n" +
                "What wisdom do you seek today?"
            );
        }
    }

    // ── Button handlers ────────────────────────────────────────────────────────

    @FXML
    private void handleSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || student == null) return;
        inputField.clear();
        addUserMessage(text);
        sendToOracle(text, false);
    }

    @FXML
    private void handleSummonWisdom() {
        if (student == null) return;
        String prompt = "Give me a full analysis of my academic standing right now. " +
                        "Cover my GPA, my best and worst courses, my attendance, and tell me " +
                        "exactly what I should do in the next 2 weeks.";
        addUserMessage("📊 Summon full analysis of my standing");
        sendToOracle(prompt, true);
    }

    @FXML
    private void handleSuggest(javafx.event.ActionEvent event) {
        // Strip the emoji prefix from the button text and use it as the question
        Button btn = (Button) event.getSource();
        String raw = btn.getText().trim();
        // Remove the leading emoji + spaces
        String question = raw.replaceFirst("^[^\\p{L}]+", "").trim();
        if (question.isEmpty()) return;
        inputField.clear();
        addUserMessage(question);
        sendToOracle(question, false);
    }

    @FXML
    private void handleClearChat() {
        chatBox.getChildren().clear();
        aiService.clearHistory();
        greeted = false;
        setStudent(student); // re-greet
    }

    // ── Core send logic ────────────────────────────────────────────────────────

    private void sendToOracle(String userMessage, boolean isSummon) {
        setLoading(true, "The Oracle is consulting the ancient tomes…");

        aiService.sendMessageAsync(student, gpa, userMessage, isSummon)
            .thenAccept(reply -> Platform.runLater(() -> {
                addOracleMessage(reply);
                setLoading(false, "");
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    addOracleMessage("⚠ The Oracle's connection faltered: " + ex.getMessage());
                    setLoading(false, "");
                });
                return null;
            });
    }

    // ── Chat bubble builders ───────────────────────────────────────────────────

    private void addUserMessage(String text) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_RIGHT);

        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(480);
        bubble.setStyle(
            "-fx-background-color: #3a2d1e;" +
            "-fx-text-fill: #e8d5a3;" +
            "-fx-font-family: 'Inter', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 16 10 16;" +
            "-fx-background-radius: 16 16 4 16;"
        );
        HBox.setMargin(bubble, new Insets(0, 0, 0, 80));
        row.getChildren().add(bubble);
        chatBox.getChildren().add(row);
    }

    private void addOracleMessage(String text) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.TOP_LEFT);

        // Avatar circle
        Label avatar = new Label("🔮");
        avatar.setStyle(
            "-fx-background-color: #1e1a16;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 6 8;" +
            "-fx-font-size: 14px;"
        );
        avatar.setMinWidth(36);
        avatar.setMinHeight(36);

        // Message bubble — render **bold** markers
        VBox bubble = new VBox(4);
        bubble.setMaxWidth(520);
        bubble.setStyle(
            "-fx-background-color: #1e1a16;" +
            "-fx-border-color: #2d251f;" +
            "-fx-border-radius: 4 16 16 16;" +
            "-fx-background-radius: 4 16 16 16;" +
            "-fx-padding: 12 16 12 16;"
        );

        // Parse the message for **bold** markers and split into Text segments
        TextFlow flow = buildTextFlow(text);
        flow.setMaxWidth(480);
        bubble.getChildren().add(flow);

        HBox.setMargin(bubble, new Insets(0, 80, 0, 0));
        row.getChildren().addAll(avatar, bubble);
        chatBox.getChildren().add(row);
    }

    /**
     * Converts a string with **bold** markers into a TextFlow with mixed
     * normal and bold Text nodes so the chat reads naturally.
     */
    private TextFlow buildTextFlow(String raw) {
        TextFlow flow = new TextFlow();
        flow.setStyle("-fx-line-spacing: 3;");
        String[] parts = raw.split("\\*\\*", -1);
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            Text t = new Text(parts[i]);
            if (i % 2 == 1) {
                // inside **...**  → bold gold
                t.setStyle(
                    "-fx-font-family: 'Inter', sans-serif;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-fill: #d4af37;"
                );
            } else {
                t.setStyle(
                    "-fx-font-family: 'Inter', sans-serif;" +
                    "-fx-font-size: 13px;" +
                    "-fx-fill: #a8a29e;"
                );
            }
            flow.getChildren().add(t);
        }
        return flow;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void setLoading(boolean on, String msg) {
        if (loadingIndicator != null) loadingIndicator.setVisible(on);
        if (statusLabel != null) statusLabel.setText(on ? msg : "");
        if (inputField != null) inputField.setDisable(on);
    }
}
