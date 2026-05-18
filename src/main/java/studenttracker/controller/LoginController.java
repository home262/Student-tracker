package studenttracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import studenttracker.App;
import studenttracker.model.Admin;
import studenttracker.model.Student;
import studenttracker.service.StudentService;

public class LoginController {

    @FXML private TextField     idField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Label         idLabel;
    @FXML private Label         hintLabel;
    @FXML private Button        scholarToggle;
    @FXML private Button        wardenToggle;

    // true = student login, false = admin login
    private boolean scholarMode = true;

    // ── Toggle handlers ──────────────────────────────────────────────────────

    @FXML
    private void switchToScholar() {
        scholarMode = true;
        idLabel.setText("Student ID");
        idField.setPromptText("e.g. S101");
        hintLabel.setText("Scholar: S101 / 1234");
        scholarToggle.getStyleClass().setAll("toggle-active");
        wardenToggle.getStyleClass().setAll("toggle-inactive");
        errorLabel.setText("");
        idField.clear();
        passwordField.clear();
    }

    @FXML
    private void switchToWarden() {
        scholarMode = false;
        idLabel.setText("Admin Code");
        idField.setPromptText("e.g. ADMIN001");
        hintLabel.setText("Warden: ADMIN001 / admin123");
        wardenToggle.getStyleClass().setAll("toggle-active");
        scholarToggle.getStyleClass().setAll("toggle-inactive");
        errorLabel.setText("");
        idField.clear();
        passwordField.clear();
    }

    // ── Login handler ────────────────────────────────────────────────────────

    @FXML
    private void handleLogin(ActionEvent event) {
        String id       = idField.getText().trim();
        String password = passwordField.getText().trim();

        if (id.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (scholarMode) {
            loginAsStudent(event, id, password);
        } else {
            loginAsAdmin(event, id, password);
        }
    }

    // ── Student login ────────────────────────────────────────────────────────

    private void loginAsStudent(ActionEvent event, String studentId, String password) {
        StudentService service = new StudentService(App.getDatabase());
        Student student = service.getStudentById(studentId);

        if (student == null) {
            errorLabel.setText("Scholar not found in the Sanctum.");
            return;
        }
        if (!student.getPassword().equals(password)) {
            errorLabel.setText("Invalid credentials. Access denied.");
            return;
        }

        App.setLoggedInStudentId(studentId);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
            Parent root = loader.load();

            MainController mainCtrl = loader.getController();
            mainCtrl.initData(student);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                javafx.scene.image.Image icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/app_icon.png"));
                stage.getIcons().setAll(icon);
            } catch (Exception ignored) {}
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setScene(new Scene(root, 1200, 750));
            stage.setTitle("Scholar's Sanctum — " + student.getName());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load the Sanctum: " + e.getMessage());
        }
    }

    // ── Admin login ──────────────────────────────────────────────────────────

    private void loginAsAdmin(ActionEvent event, String adminCode, String password) {
        Admin admin = App.getDatabase().getAdminByCode(adminCode);

        if (admin == null) {
            errorLabel.setText("Warden not recognised in the Sanctum.");
            return;
        }
        if (!admin.getPassword().equals(password)) {
            errorLabel.setText("Invalid credentials. Access denied.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/admin_dashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController ctrl = loader.getController();
            ctrl.initData(admin);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                javafx.scene.image.Image icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/app_icon.png"));
                stage.getIcons().setAll(icon);
            } catch (Exception ignored) {}
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setScene(new Scene(root, 1200, 750));
            stage.setTitle("Scholar's Sanctum — Warden's Keep");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load Warden's Keep: " + e.getMessage());
        }
    }
}
