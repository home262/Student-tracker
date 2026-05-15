package studenttracker.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import studenttracker.App;
import studenttracker.model.Student;
import studenttracker.service.StudentService;

public class LoginController {

    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String studentId = studentIdField.getText().trim();
        String password  = passwordField.getText().trim();

        if (studentId.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        StudentService service = new StudentService(App.getDatabase());
        Student student = service.getStudentById(studentId);

        if (student == null) {
            errorLabel.setText("Student not found in the Sanctum.");
            return;
        }

        // Allow login by password match directly (simple auth)
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
            Scene scene = new Scene(root, 1200, 750);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setTitle("Scholar's Sanctum — " + student.getName());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load the Sanctum: " + e.getMessage());
        }
    }
}
