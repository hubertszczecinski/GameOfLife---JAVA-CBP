package progkom.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import progkom.GameOfLifeBoard;
import progkom.PlainGameOfLifeSimulator;
import progkom.ValidationException;
import progkom.gui.Density;
import progkom.gui.SimulationScene;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFormController {
    private static final Logger LOGGER = Logger.getLogger(MainFormController.class.getName());

    @FXML
    private TextField sizeInput;

    @FXML
    private ComboBox<String> fillLevelCombo;

    @FXML
    private Button startButton;

    @FXML
    private ComboBox<String> languageSelector;

    private Stage primaryStage;
    private Locale locale;

    public void init(Stage primaryStage, Locale locale) {
        this.primaryStage = primaryStage;
        this.locale = locale;

        // Ustawienie domyślnego języka (elementy są już zdefiniowane w FXML)
        languageSelector.setValue(locale.getLanguage().equals("pl") ? "Polski" : "English");

        languageSelector.setOnAction(event -> onLanguageChange(primaryStage));

        fillLevelCombo.getItems().addAll("Low (10%)", "Medium (30%)", "High (50%)");
        fillLevelCombo.getSelectionModel().selectFirst();

        configureSizeInput();

        startButton.setOnAction(e -> startSimulation());
    }

    private void configureSizeInput() {
        sizeInput.setTextFormatter(new javafx.scene.control.TextFormatter<>(
                change -> (change.getControlNewText().matches("\\d{0,2}")
                        && Integer.parseInt(change.getControlNewText().isEmpty() ? "0"
                        : change.getControlNewText()) <= 14) ? change : null
        ));
    }

    private void onLanguageChange(Stage primaryStage) {
        String selectedLanguage = languageSelector.getValue();
        Locale newLocale = selectedLanguage.equals("Polski") ? new Locale("pl", "PL") : new Locale("en", "US");

        reloadScene(primaryStage, newLocale);
    }

    private void reloadScene(Stage primaryStage, Locale newLocale) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));
            loader.setResources(ResourceBundle.getBundle("messages", newLocale));
            Parent root = loader.load();

            MainFormController controller = loader.getController();
            controller.init(primaryStage, newLocale);

            primaryStage.getScene().setRoot(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reloading scene: {0}", e.getMessage());
            LOGGER.log(Level.SEVERE, "Exception stack trace:", e);
        }
    }

    private void startSimulation() {
        try {
            String sizeInputValue = sizeInput.getText().trim();
            int size = Integer.parseInt(sizeInputValue);

            if (size < 4 || size > 14) {
                sizeInput.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: #ffe6e6;");
                throw new ValidationException("Board size must be between 4 and 14.");
            } else {
                sizeInput.setStyle("-fx-border-color: #ced4da; -fx-border-width: 1px; -fx-background-color: white;");
            }

            String densityLevel = fillLevelCombo.getValue();
            Density density = Density.valueOf(densityLevel.toUpperCase().split(" ")[0]);

            PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
            GameOfLifeBoard board = new GameOfLifeBoard(size, size, simulator);

            density.applyDensity(board);

            SimulationScene simulationScene = new SimulationScene(board, locale);
            primaryStage.setScene(simulationScene.getScene());

            LOGGER.log(Level.INFO, "Simulation started successfully with size {0} and density {1}.",
                    new Object[]{size, densityLevel});
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid board size entered: {0}", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error starting simulation.", ex);
        }
    }
}
