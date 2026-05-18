package studenttracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import studenttracker.App;
import studenttracker.model.Student;
import studenttracker.service.AIAdvisorService;
import studenttracker.service.StudentService;

public class AdvisorController implements StudentAware {

    @FXML private TextArea   oracleTextArea;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label      statusLabel;

    private Student student;
    private final AIAdvisorService aiService = new AIAdvisorService();

    @FXML
    public void initialize() {
        if (loadingIndicator != null) loadingIndicator.setVisible(false);
    }

    @Override
    public void setStudent(Student student) {
        this.student = student;
    }

    @FXML
    private void handleSummonWisdom() {
        if (student == null) return;

        oracleTextArea.setText("");
        oracleTextArea.setOpacity(0.5);
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        if (statusLabel != null) statusLabel.setText("The Oracle is reading your scrolls...");

        StudentService studentService = new StudentService(App.getDatabase());
        double gpa = studentService.calculateGPA(student);

        aiService.getAdviceAsync(student, gpa, student.getGrades(), student.getAttendanceRecords())
                .thenAccept(advice -> {
                    javafx.application.Platform.runLater(() -> {
                        oracleTextArea.setText(advice);
                        oracleTextArea.setOpacity(1.0);
                        if (loadingIndicator != null) loadingIndicator.setVisible(false);
                        if (statusLabel != null) statusLabel.setText("");
                    });
                });
    }
}
