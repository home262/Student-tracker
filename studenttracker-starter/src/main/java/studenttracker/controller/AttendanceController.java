package studenttracker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import studenttracker.App;
import studenttracker.model.Attendance;
import studenttracker.model.Student;
import studenttracker.service.StudentService;

import java.time.LocalDate;
import java.util.List;

public class AttendanceController implements StudentAware {

    @FXML private Label presenceRateLabel;
    @FXML private Label presentCountLabel;
    @FXML private Label absentCountLabel;

    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, LocalDate> dateCol;
    @FXML private TableColumn<Attendance, String> courseCol;
    @FXML private TableColumn<Attendance, String> statusCol;

    private Student student;

    @Override
    public void setStudent(Student student) {
        this.student = student;
        initTable();
        populateData();
    }

    private void initTable() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        courseCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCourse() != null ? 
                cellData.getValue().getCourse().getCourseName() : "Unknown"));
        
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void populateData() {
        if (student == null) return;

        StudentService service = new StudentService(App.getDatabase());
        List<Attendance> records = student.getAttendanceRecords();
        
        attendanceTable.setItems(FXCollections.observableArrayList(records));

        double rate = service.calculateAttendanceRate(student);
        presenceRateLabel.setText(String.format("%.0f%%", rate));

        long present = records.stream().filter(r -> r.getStatus().equalsIgnoreCase("Present")).count();
        long absent = records.stream().filter(r -> r.getStatus().equalsIgnoreCase("Absent")).count();

        presentCountLabel.setText(String.valueOf(present));
        absentCountLabel.setText(String.valueOf(absent));
    }
}
