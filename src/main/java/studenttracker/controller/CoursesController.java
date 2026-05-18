package studenttracker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import studenttracker.model.Course;
import studenttracker.model.Student;

import java.util.List;

public class CoursesController implements StudentAware {

    @FXML private Label enrolledCountLabel;
    @FXML private Label creditHoursLabel;

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> idCol;
    @FXML private TableColumn<Course, String> nameCol;
    @FXML private TableColumn<Course, String> instructorCol;
    @FXML private TableColumn<Course, Integer> creditsCol;
    @FXML private TableColumn<Course, String> semesterCol;

    private Student student;

    @Override
    public void setStudent(Student student) {
        this.student = student;
        initTable();
        populateData();
    }

    private void initTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructorName"));
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
    }

    private void populateData() {
        if (student == null) return;

        List<Course> courses = student.getCourses();
        coursesTable.setItems(FXCollections.observableArrayList(courses));

        enrolledCountLabel.setText(String.valueOf(courses.size()));
        
        int totalCredits = courses.stream().mapToInt(Course::getCreditHours).sum();
        creditHoursLabel.setText(String.valueOf(totalCredits));
    }
}
