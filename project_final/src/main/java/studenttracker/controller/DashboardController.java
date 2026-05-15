package studenttracker.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import studenttracker.App;
import studenttracker.model.Attendance;
import studenttracker.model.Course;
import studenttracker.model.Grade;
import studenttracker.model.Student;
import studenttracker.service.StudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController implements StudentAware {

    @FXML private Label gpaLabel;
    @FXML private Label presenceLabel;
    @FXML private Label presenceHint;
    @FXML private Label questsLabel;
    @FXML private Label omensLabel;
    @FXML private VBox recentGradesBox;
    @FXML private VBox omensDetailBox;

    // Stat card icons
    @FXML private ImageView iconGpa;
    @FXML private ImageView iconPresence;
    @FXML private ImageView iconQuests;
    @FXML private ImageView iconOmens;

    // Action button icons
    @FXML private ImageView iconForge;
    @FXML private ImageView iconViewGrades;
    @FXML private ImageView iconCheckAtt;
    @FXML private ImageView iconOracle;

    private Student student;

    @FXML
    public void initialize() {
        loadIcon(iconGpa,       "/assets/icons/shield.png");
        loadIcon(iconPresence,  "/assets/icons/castle.png");
        loadIcon(iconQuests,    "/assets/icons/sword.png");
        loadIcon(iconOmens,     "/assets/icons/stone.png");

        loadIcon(iconForge,      "/assets/icons/manuscript.png");
        loadIcon(iconViewGrades, "/assets/icons/table.png");
        loadIcon(iconCheckAtt,   "/assets/icons/book.png");
        loadIcon(iconOracle,     "/assets/icons/wizard.png");
    }

    private void loadIcon(ImageView view, String resourcePath) {
        if (view == null) return;
        try {
            var stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                view.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + resourcePath);
        }
    }

    @Override
    public void setStudent(Student student) {
        this.student = student;
        populateDashboard();
    }

    private void populateDashboard() {
        if (student == null) return;

        StudentService service = new StudentService(App.getDatabase());

        // GPA
        double gpa = service.calculateGPA(student);
        gpaLabel.setText(String.format("%.2f", gpa));

        // Attendance rate
        double rate = service.calculateAttendanceRate(student);
        presenceLabel.setText(String.format("%.0f%%", rate));
        if (rate < 75) {
            presenceHint.setText("Recover ground in hall sessions");
            presenceHint.setStyle("-fx-text-fill: #e66777; -fx-font-size: 12px;");
        } else {
            presenceHint.setText("Good standing");
            presenceHint.setStyle("-fx-text-fill: #a8a29e; -fx-font-size: 12px;");
        }

        // Courses count
        List<Course> courses = student.getCourses();
        questsLabel.setText(String.valueOf(courses.size()));

        // Dark omens: courses with >= 3 absences
        Map<String, Integer> absenceMap = new HashMap<>();
        for (Attendance a : student.getAttendanceRecords()) {
            if (a.getStatus().equalsIgnoreCase("Absent")) {
                String cName = a.getCourse() != null ? a.getCourse().getCourseName() : "Unknown Course";
                absenceMap.merge(cName, 1, Integer::sum);
            }
        }
        
        int omenCount = 0;
        omensDetailBox.getChildren().clear();
        for (Map.Entry<String, Integer> entry : absenceMap.entrySet()) {
            if (entry.getValue() >= 3) {
                omenCount++;
                VBox omenItem = createOmenItem(entry.getKey(), entry.getValue());
                omensDetailBox.getChildren().add(omenItem);
            }
        }
        omensLabel.setText(String.valueOf(omenCount));

        if (omenCount == 0) {
            Label noOmen = new Label("No dark omens — your path is clear.");
            noOmen.setStyle("-fx-text-fill: #8b847a; -fx-font-size: 12px;");
            omensDetailBox.getChildren().add(noOmen);
        }

        // Recent grades
        recentGradesBox.getChildren().clear();
        List<Grade> grades = student.getGrades();
        int limit = Math.min(grades.size(), 5);
        for (int i = 0; i < limit; i++) {
            Grade g = grades.get(i);
            HBox row = createGradeRow(g);
            recentGradesBox.getChildren().add(row);
        }
        if (grades.isEmpty()) {
            Label noGrades = new Label("No battle scores recorded yet.");
            noGrades.setStyle("-fx-text-fill: #8b847a; -fx-font-size: 12px;");
            recentGradesBox.getChildren().add(noGrades);
        }
    }

    private HBox createGradeRow(Grade g) {
        HBox row = new HBox(15);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #1a1614; -fx-border-color: #2d251f; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15;");

        String courseName = g.getCourse() != null ? g.getCourse().getCourseName() : "Course " + g.getCourse().getCourseId();
        Label nameLabel = new Label(courseName);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        String letter = g.calculateLetter();
        Label badge = new Label(letter);
        badge.setStyle(getBadgeStyle(letter));
        badge.setPadding(new Insets(2, 8, 2, 8));

        Label scoreLabel = new Label(String.format("%.0f%%", g.getScore()));
        scoreLabel.setStyle("-fx-text-fill: #8b847a; -fx-font-size: 12px;");

        row.getChildren().addAll(nameLabel, badge, scoreLabel);
        return row;
    }

    private VBox createOmenItem(String courseName, int absences) {
        VBox box = new VBox(3);
        box.setStyle("-fx-background-color: #211113; -fx-border-color: #e66777 transparent transparent transparent; -fx-border-width: 2 0 0 0; -fx-padding: 10;");
        Label name = new Label(courseName);
        name.setStyle("-fx-text-fill: #e66777; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label desc = new Label(absences + " absences recorded — stay vigilant");
        desc.setStyle("-fx-text-fill: #a8a29e; -fx-font-size: 11px;");
        box.getChildren().addAll(name, desc);
        return box;
    }

    private String getBadgeStyle(String letter) {
        return switch (letter) {
            case "A" -> "-fx-background-color: #1e3a22; -fx-text-fill: #7dd887; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4;";
            case "B" -> "-fx-background-color: #273646; -fx-text-fill: #8fb2d8; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4;";
            case "C" -> "-fx-background-color: #463c27; -fx-text-fill: #d8c28f; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4;";
            case "D" -> "-fx-background-color: #463127; -fx-text-fill: #d8a08f; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4;";
            default  -> "-fx-background-color: #462727; -fx-text-fill: #d88f8f; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4;";
        };
    }
}
