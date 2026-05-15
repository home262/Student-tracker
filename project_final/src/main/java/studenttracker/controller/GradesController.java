package studenttracker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import studenttracker.App;
import studenttracker.model.Grade;
import studenttracker.model.Student;
import studenttracker.service.StudentService;

import java.util.List;

public class GradesController implements StudentAware {

    @FXML private Label semGpaLabel;
    @FXML private Label highScoreLabel;
    @FXML private Label lowScoreLabel;
    @FXML private Label courseCountLabel;

    @FXML private TableView<Grade> gradesTable;
    @FXML private TableColumn<Grade, String> courseCol;
    @FXML private TableColumn<Grade, String> instructorCol;
    @FXML private TableColumn<Grade, Double> scoreCol;
    @FXML private TableColumn<Grade, String> letterCol;
    @FXML private TableColumn<Grade, String> semesterCol;

    private Student student;

    @Override
    public void setStudent(Student student) {
        this.student = student;
        initTable();
        populateData();
    }

    private void initTable() {
        courseCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCourse() != null ? 
                cellData.getValue().getCourse().getCourseName() : "Unknown"));
        
        instructorCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCourse() != null ? 
                cellData.getValue().getCourse().getInstructorName() : "Unknown"));
        
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        letterCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().calculateLetter()));
        
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
    }

    private void populateData() {
        if (student == null) return;

        StudentService service = new StudentService(App.getDatabase());
        List<Grade> grades = student.getGrades();
        
        gradesTable.setItems(FXCollections.observableArrayList(grades));

        // GPA
        semGpaLabel.setText(String.format("%.2f", service.calculateGPA(student)));
        
        // High/Low scores
        double high = 0;
        double low = 100;
        if (!grades.isEmpty()) {
            for (Grade g : grades) {
                if (g.getScore() > high) high = g.getScore();
                if (g.getScore() < low) low = g.getScore();
            }
            highScoreLabel.setText(String.format("%.0f%%", high));
            lowScoreLabel.setText(String.format("%.0f%%", low));
        } else {
            highScoreLabel.setText("—");
            lowScoreLabel.setText("—");
        }

        courseCountLabel.setText(String.valueOf(student.getCourses().size()));
    }
}
