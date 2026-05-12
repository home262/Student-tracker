package studenttracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            errorLabel.setVisible(true);
            return;
        }

        // TODO: connect to your DatabaseManager / UserService here
        System.out.println("Logging in: " + email);
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleForgotPassword() {
        System.out.println("Forgot password clicked");
    }

    @FXML
    private void handleRegister() {
        System.out.println("Register clicked");
    }
}