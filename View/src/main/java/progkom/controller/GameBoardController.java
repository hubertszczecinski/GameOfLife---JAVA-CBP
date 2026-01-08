package progkom.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import progkom.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameBoardController {
    private static final String FILE_PATH = "game_of_life_board.dat";
    private static final Logger LOGGER = Logger.getLogger(GameBoardController.class.getName());

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Button nextStepButton;

    @FXML
    private Button resetButton;

    @FXML
    private ComboBox<String> languageSelector;

    private GameOfLifeBoard board;

    private ResourceBundle bundle;

    public void init(GameOfLifeBoard board, Locale locale) {
        this.board = board;
        bundle = ResourceBundle.getBundle("messages", locale);

        nextStepButton.setText(bundle.getString("nextStep.button"));
        resetButton.setText(bundle.getString("reset.button"));

        nextStepButton.setOnAction(e -> doNextStep());
        resetButton.setOnAction(e -> resetBoard());
        gameCanvas.setOnMouseClicked(this::handleCellClick);

        drawBoard();
    }

    @FXML
    public void saveBoard() {
        try (var dao = new FileGameOfLifeBoardDao(FILE_PATH)) {
            dao.write(board);
            LOGGER.log(Level.INFO, "Board saved successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save board.", e);
        }
    }

    @FXML
    public void loadBoard() {
        try (var dao = new FileGameOfLifeBoardDao(FILE_PATH)) {
            board = dao.read();
            drawBoard();
            LOGGER.log(Level.INFO, "Board loaded successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load board.", e);
        }
    }

    @FXML
    public void saveBoardToDb() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(bundle.getString("saveDb.title"));
        dialog.setHeaderText(bundle.getString("saveDb.header"));
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(boardName -> {
            if (boardName == null || boardName.trim().isEmpty()) {
                showErrorAlert("Błąd", "Nazwa planszy nie może być pusta.");
                return;
            }
            try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.createJdbcDao(boardName.trim())) {
                dao.write(board);
                LOGGER.log(Level.INFO, "Board saved to database successfully.");
                showSuccessAlert("Sukces", "Plansza została zapisana do bazy danych: " + boardName.trim());
            } catch (DatabaseOperationException e) {
                LOGGER.log(Level.SEVERE, "Failed to save board to database.", e);
                showErrorAlert("Błąd zapisu", "Nie udało się zapisać planszy do bazy danych.\n" + e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to save board to database.", e);
                showErrorAlert("Błąd", "Wystąpił nieoczekiwany błąd podczas zapisu:\n" + e.getMessage());
            }
        });
    }


    @FXML
    public void loadBoardFromDb() {
        // Dao do wczytania nazw plansz
        try (JdbcGameOfLifeBoardDao namesDao =
                     (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao("loading")) {
            List<String> boardNames = namesDao.names();
            if (boardNames.isEmpty()) {
                LOGGER.log(Level.WARNING, "No boards available in the database.");
                showInfoAlert("Informacja", "Brak plansz w bazie danych.");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(boardNames.getFirst(), boardNames);
            dialog.setTitle(bundle.getString("loadDb.title"));
            dialog.setHeaderText(bundle.getString("loadDb.header"));
            dialog.setContentText(bundle.getString("loadDb.content"));
            Optional<String> selectedBoard = dialog.showAndWait();

            if (selectedBoard.isPresent()) {
                String boardName = selectedBoard.get();
                LOGGER.log(Level.INFO, "Loading board: {0}", boardName);
                // Dao do odczytania planszy o podanej nazwie
                try (JdbcGameOfLifeBoardDao readingDao =
                             (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao(boardName)) {
                    board = readingDao.read();
                    drawBoard();
                    LOGGER.log(Level.INFO, "Board loaded successfully.");
                    showSuccessAlert("Sukces", "Plansza została wczytana z bazy danych: " + boardName);
                } catch (DatabaseOperationException e) {
                    LOGGER.log(Level.SEVERE, "Failed to load board from database.", e);
                    showErrorAlert("Błąd wczytywania",
                            "Nie udało się wczytać planszy z bazy danych.\n" + e.getMessage());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to load board from database.", e);
                    showErrorAlert("Błąd", "Wystąpił nieoczekiwany błąd podczas wczytywania:\n" + e.getMessage());
                }
            } else {
                LOGGER.log(Level.INFO, "Board loading cancelled by user.");
            }
        } catch (DatabaseOperationException e) {
            LOGGER.log(Level.SEVERE, "Failed to load board from database.", e);
            showErrorAlert("Błąd połączenia", "Nie udało się połączyć z bazą danych.\n" + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load board from database.", e);
            showErrorAlert("Błąd", "Wystąpił nieoczekiwany błąd:\n" + e.getMessage());
        }
    }


    private void drawBoard() {
        GraphicsContext graphics = gameCanvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        double cellWidth = gameCanvas.getWidth() / board.getColumns();
        double cellHeight = gameCanvas.getHeight() / board.getRows();

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                boolean cellValue = board.getCell(i, j).getCellValue();

                graphics.setFill(cellValue ? Color.GREEN : Color.LIGHTGRAY);
                graphics.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                graphics.setStroke(Color.DARKGRAY);
                graphics.strokeRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    private void handleCellClick(MouseEvent event) {
        double cellWidth = gameCanvas.getWidth() / board.getColumns();
        double cellHeight = gameCanvas.getHeight() / board.getRows();

        int column = (int) (event.getX() / cellWidth);
        int row = (int) (event.getY() / cellHeight);

        boolean currentValue = board.getCell(row, column).getCellValue();
        board.setCellValue(row, column, !currentValue);

        LOGGER.log(Level.FINE, "Cell clicked at row {0}, column {1}. New value: {2}",
                new Object[]{row, column, !currentValue});

        drawBoard();
    }

    private void doNextStep() {
        board.doSimulationStep();
        LOGGER.log(Level.INFO, "Performed simulation step.");
        drawBoard();
    }

    private void resetBoard() {
        board.clear();
        LOGGER.log(Level.INFO, "Board reset.");
        drawBoard();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
