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

    @FXML private TextArea oracleTextArea;
    @FXML private ProgressIndicator loadingIndicator;

    private Student student;
    private final AIAdvisorService aiService = new AIAdvisorService();

    @Override
    public void setStudent(Student student) {
        this.student = student;
    }

    @FXML
    private void handleSummonWisdom() {
        if (student == null) return;

        oracleTextArea.setOpacity(0.5);
        loadingIndicator.setVisible(true);

        StudentService studentService = new StudentService(App.getDatabase());
        double gpa = studentService.calculateGPA(student);

        aiService.getAdviceAsync(student, gpa, student.getGrades(), student.getAttendanceRecords())
                .thenAccept(advice -> {
                    javafx.application.Platform.runLater(() -> {
                        oracleTextArea.setText(advice);
                        oracleTextArea.setOpacity(1.0);
                        loadingIndicator.setVisible(false);
                    });
                });
    }
}
