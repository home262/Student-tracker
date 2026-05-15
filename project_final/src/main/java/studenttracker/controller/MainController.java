package studenttracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
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

    @FXML private void handleExport(ActionEvent e)  {
        // TODO: implement CSV export
        setActive(navExport);
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

            contentArea.getChildren().setAll(view);
        } catch (IOException ex) {
            ex.printStackTrace();
            Label err = new Label("Failed to load view: " + fxmlPath);
            err.setStyle("-fx-text-fill: #e66777; -fx-font-size: 14px;");
            contentArea.getChildren().setAll(err);
        }
    }
}
