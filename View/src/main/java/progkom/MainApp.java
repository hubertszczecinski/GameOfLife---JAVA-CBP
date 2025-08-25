package progkom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import progkom.controller.MainFormController;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    @Override
    public void start(Stage primaryStage) {
        Locale defaultLocale = new Locale("pl", "PL");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));

            loader.setResources(ResourceBundle.getBundle("messages", defaultLocale));
            Parent root = loader.load();

            MainFormController controller = loader.getController();
            controller.init(primaryStage, defaultLocale);

            primaryStage.setTitle("Game of Life - Configuration");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load MainForm.fxml", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
