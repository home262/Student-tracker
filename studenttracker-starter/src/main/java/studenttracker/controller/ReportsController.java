package studenttracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import studenttracker.model.Student;

public class ReportsController implements StudentAware {

    @FXML private Label studentNameLabel;

    private Student student;

    @Override
    public void setStudent(Student student) {
        this.student = student;
        if (student != null) {
            studentNameLabel.setText(student.getName());
        }
    }
}
