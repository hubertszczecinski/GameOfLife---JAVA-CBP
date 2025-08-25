package progkom.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import progkom.BaseApplicationException;
import progkom.controller.MainFormController;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigScene {
    private static final Logger LOGGER = Logger.getLogger(ConfigScene.class.getName());
    private final Scene scene;

    public ConfigScene(Stage primaryStage, Locale locale) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));
            loader.setResources(ResourceBundle.getBundle("messages", locale));
            Parent root = loader.load();

            MainFormController controller = loader.getController();
            controller.init(primaryStage, locale);

            this.scene = new Scene(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot load MainForm.fxml", e);
            throw new BaseApplicationException("Cannot load MainForm.fxml", e);
        }
    }

    public Scene getScene() {
        return scene;
    }
}
