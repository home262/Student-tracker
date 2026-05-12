package studenttracker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginFrame {

    public static void show(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        LoginFrame.class.getResource("/views/login.fxml")
                )
        );
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Student Tracker — Login");
        stage.setResizable(false);
        stage.show();
    }
}