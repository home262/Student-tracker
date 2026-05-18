package studenttracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import studenttracker.database.DatabaseManager;

public class App extends Application {

    private static DatabaseManager databaseManager;
    private static String loggedInStudentId;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Init DB
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        databaseManager.createAllTables();

        // Load login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
        Parent root = loader.load();

        // ── App icon — appears in window title bar AND taskbar ────────────────
        try {
            Image icon = new Image(getClass().getResourceAsStream("/assets/app_icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setTitle("Scholar's Sanctum");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
    }

    public static DatabaseManager getDatabase() {
        return databaseManager;
    }

    public static String getLoggedInStudentId() {
        return loggedInStudentId;
    }

    public static void setLoggedInStudentId(String id) {
        loggedInStudentId = id;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
