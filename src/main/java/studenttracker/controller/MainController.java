package studenttracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import studenttracker.App;
import studenttracker.model.Student;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label profileName;
    @FXML private Label profileRank;
    @FXML private Label avatarLetter;
    @FXML private Circle avatarCircle;

    @FXML private ImageView logoIcon;
    @FXML private ImageView iconHome;
    @FXML private ImageView iconGrades;
    @FXML private ImageView iconCourses;
    @FXML private ImageView iconAttendance;
    @FXML private ImageView iconReports;
    @FXML private ImageView iconExport;
    @FXML private ImageView iconAdvisor;

    @FXML private Button navHomeKeep;
    @FXML private Button navGrades;
    @FXML private Button navCourses;
    @FXML private Button navAttendance;
    @FXML private Button navReports;
    @FXML private Button navExport;
    @FXML private Button navAdvisor;
    @FXML private Button navLogout;
    @FXML private ImageView iconLogout;

    private Student currentStudent;
    private Button activeButton;


    @FXML
    public void initialize() {
        loadIcon(logoIcon, "/assets/icons/castle.png");
        loadIcon(iconHome, "/assets/icons/shield.png");
        loadIcon(iconGrades, "/assets/icons/book.png");
        loadIcon(iconCourses, "/assets/icons/manuscript.png");
        loadIcon(iconAttendance, "/assets/icons/table.png");
        loadIcon(iconReports, "/assets/icons/report.png");
        loadIcon(iconExport, "/assets/icons/sword.png");
        loadIcon(iconAdvisor, "/assets/icons/robot.png");
        loadIcon(iconLogout, "/assets/icons/sword2.png");
    }

    private void loadIcon(ImageView view, String path) {
        try {
            view.setImage(new Image(getClass().getResourceAsStream(path)));
        } catch (Exception ignored) {
        }
    }

    public void initData(Student student) {
        this.currentStudent = student;
        profileName.setText(student.getName());
        avatarLetter.setText(student.getName().substring(0, 1).toUpperCase());
        profileRank.setText("Scholar rank · Steady");

        activeButton = navHomeKeep;
        loadView("/ui/dashboard_content.fxml");
    }

    // ---------- Navigation ----------

    @FXML private void showDashboard(ActionEvent e) { setActive(navHomeKeep); loadView("/ui/dashboard_content.fxml"); }
    @FXML private void showGrades(ActionEvent e)    { setActive(navGrades);   loadView("/ui/grades.fxml"); }
    @FXML private void showCourses(ActionEvent e)   { setActive(navCourses);  loadView("/ui/courses.fxml"); }
    @FXML private void showAttendance(ActionEvent e){ setActive(navAttendance);loadView("/ui/attendance.fxml"); }
    @FXML private void showReports(ActionEvent e)   { setActive(navReports);  loadView("/ui/reports.fxml"); }
    @FXML private void showAdvisor(ActionEvent e)   { setActive(navAdvisor);  loadView("/ui/advisor.fxml"); }

    /** Called by DashboardController quick-action buttons to navigate here. */
    public void navigateTo(String section) {
        switch (section) {
            case "grades"     -> { setActive(navGrades);     loadView("/ui/grades.fxml"); }
            case "attendance" -> { setActive(navAttendance); loadView("/ui/attendance.fxml"); }
            case "reports"    -> { setActive(navReports);    loadView("/ui/reports.fxml"); }
            case "advisor"    -> { setActive(navAdvisor);    loadView("/ui/advisor.fxml"); }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                Image icon = new Image(getClass().getResourceAsStream("/assets/app_icon.png"));
                stage.getIcons().setAll(icon);
            } catch (Exception ignored) {}
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setScene(new Scene(root, 500, 600));
            stage.setTitle("Scholar's Sanctum");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleExport(ActionEvent e) {
        setActive(navExport);
        if (currentStudent == null) return;
        try {
            studenttracker.service.StudentService svc =
                new studenttracker.service.StudentService(App.getDatabase());
            double gpa = svc.calculateGPA(currentStudent);
            double attRate = svc.calculateAttendanceRate(currentStudent);
            String filename = "export_" + currentStudent.getStudentId() + "_" +
                java.time.LocalDate.now().toString().replace("-","") + ".csv";
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
                pw.println("Scholar's Sanctum — Quick Export");
                pw.println("Student," + currentStudent.getName());
                pw.println("ID," + currentStudent.getStudentId());
                pw.println("Department," + currentStudent.getDepartment());
                pw.println("GPA," + String.format("%.2f", gpa));
                pw.println("Attendance," + String.format("%.0f%%", attRate));
                pw.println();
                pw.println("Course,Instructor,Credits,Score,Grade");
                java.util.Map<String,studenttracker.model.Grade> gMap = new java.util.HashMap<>();
                for (studenttracker.model.Grade g : currentStudent.getGrades())
                    if (g.getCourse() != null) gMap.put(g.getCourse().getCourseId(), g);
                for (studenttracker.model.Course c : currentStudent.getCourses()) {
                    studenttracker.model.Grade g = gMap.get(c.getCourseId());
                    pw.println(c.getCourseName() + "," + c.getInstructorName() + "," +
                        c.getCreditHours() + "," +
                        (g != null ? String.format("%.1f", g.getScore()) : "N/A") + "," +
                        (g != null ? g.calculateLetter() : "N/A"));
                }
            }
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
            a.setTitle("Export Complete");
            a.setHeaderText("Your scroll has been forged.");
            a.setContentText("Saved as: " + filename);
            a.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------- Helpers ----------

    private void setActive(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        btn.getStyleClass().add("nav-button-active");
        activeButton = btn;
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            // Pass student data to controller if it supports it
            Object ctrl = loader.getController();
            if (ctrl instanceof StudentAware sa) {
                sa.setStudent(currentStudent);
            }
            // Pass self-reference so DashboardController can trigger navigation
            if (ctrl instanceof DashboardController dc) {
                dc.setMainController(this);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException ex) {
            ex.printStackTrace();
            Label err = new Label("Failed to load view: " + fxmlPath);
            err.setStyle("-fx-text-fill: #e66777; -fx-font-size: 14px;");
            contentArea.getChildren().setAll(err);
        }
    }
}
