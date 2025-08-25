package progkom.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import progkom.BaseApplicationException;
import progkom.GameOfLifeBoard;
import progkom.controller.GameBoardController;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulationScene {
    private static final Logger LOGGER = Logger.getLogger(SimulationScene.class.getName());
    private final Scene scene;

    public SimulationScene(GameOfLifeBoard board, Locale locale) {
        try {
            // Load the GameBoard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameBoard.fxml"));
            loader.setResources(ResourceBundle.getBundle("messages", locale));
            Parent root = loader.load();

            // Initialize the controller
            GameBoardController controller = loader.getController();
            controller.init(board, locale);

            this.scene = new Scene(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot load GameBoard.fxml", e);
            throw new BaseApplicationException("Cannot load GameBoard.fxml", e);
        }
    }

    public Scene getScene() {
        return scene;
    }
}
