package progkom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcGameOfLifeBoardDao implements Dao<GameOfLifeBoard> {
    private final String url;
    private final String user;
    private final String password;
    private final String boardName;
    private static final Logger logger = LoggerFactory.getLogger(JdbcGameOfLifeBoardDao.class);

    static {
        try {
            // Jawna rejestracja drivera PostgreSQL dla Java 21+
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL driver załadowany pomyślnie");
        } catch (ClassNotFoundException e) {
            logger.error("Nie udało się załadować drivera PostgreSQL", e);
            throw new RuntimeException("PostgreSQL driver nie został znaleziony", e);
        }
    }

    public JdbcGameOfLifeBoardDao(String url, String user, String password, String boardName) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.boardName = boardName;
        logger.debug("Utworzono JdbcGameOfLifeBoardDao dla planszy: {}, URL: {}", boardName, url);
    }

    private Connection getConnection() throws SQLException {
        String connectionUrl = url + "?connectTimeout=5&socketTimeout=10";
        logger.debug("Próba połączenia z bazą: {}", connectionUrl);
        return DriverManager.getConnection(connectionUrl, user, password);
    }

    @Override
    public GameOfLifeBoard read() {
        GameOfLifeBoard board = null;
        logger.info("Laczenie z baza danych");
        try (Connection conn = getConnection()) {
            int boardId = 0;

            logger.info("Pobieranie danych o planszy");
            try (PreparedStatement statement = conn.prepareStatement(
                    "SELECT id, rows, columns FROM board "
                        + "WHERE name = ?;")) {
                statement.setString(1, boardName);
                try (ResultSet boardSet = statement.executeQuery()) {
                    if (boardSet.next()) {
                        boardId = boardSet.getInt(1);
                        board = new GameOfLifeBoard(boardSet.getInt(2), boardSet.getInt(3),
                                new PlainGameOfLifeSimulator());
                    } else {
                        logger.error("Plansza o podanej nazwie nie istnieje");
                        throw new DatabaseOperationException("database.error.board_not_exists");
                    }
                }
            } catch (SQLException e) {
                logger.error("Nie udalo sie pobrac danych o planszy");
                throw new DatabaseOperationException("database.error.board_data_failure", e);
            }

            logger.info("Pobieranie danych o komorkach planszy");
            try (PreparedStatement statement = conn.prepareStatement(
                    "SELECT x, y, value FROM cell "
                        + "WHERE board_id = ?;")) {
                statement.setInt(1, boardId);
                try (ResultSet cellsSet = statement.executeQuery()) {
                    while (cellsSet.next()) {
                        board.setCellValue(cellsSet.getInt(1), cellsSet.getInt(2), cellsSet.getBoolean(3));
                    }
                }
            } catch (SQLException e) {
                logger.error("Nie udalo sie pobrac danych o komorkach planszy");
                throw new DatabaseOperationException("database.error.board_data_failure", e);
            }

        logger.info("Pomyslnie wczytano dane o planszy i jej komorkach");
        } catch (SQLException e) {
            logger.error("Nie udalo sie polaczyc z baza danych. URL: {}, User: {}, Error: {}",
                    url, user, e.getMessage(), e);
            throw new DatabaseOperationException("database.error.connection_failure: " + e.getMessage(), e);
        }
        return board;
    }

    @Override
    public void write(GameOfLifeBoard board) {
        logger.info("Laczenie z baza danych");
        try (Connection conn = getConnection()) {
            logger.info("Zapisywanie planszy do bazy danych");
            conn.setAutoCommit(false);

            logger.info("Sprawdzanie czy plansza juz istnieje");
            int boardId = -1;
            boolean boardExists = false;
            try (PreparedStatement statement = conn.prepareStatement(
                    "SELECT id FROM board WHERE name = ?;")) {
                statement.setString(1, boardName);
                try (ResultSet boardSet = statement.executeQuery()) {
                    if (boardSet.next()) {
                        boardId = boardSet.getInt(1);
                        boardExists = true;
                        logger.info("Plansza juz istnieje, bedzie zaktualizowana");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Nie udalo sie sprawdzic czy plansza istnieje");
                throw new DatabaseOperationException("database.error.board_selection_failure", e);
            }

            if (boardExists) {
                // Aktualizacja istniejącej planszy
                logger.info("Aktualizowanie istniejącej planszy");
                try (PreparedStatement statement = conn.prepareStatement(
                        "UPDATE board SET rows = ?, columns = ? WHERE id = ?;")) {
                    statement.setInt(1, board.getRows());
                    statement.setInt(2, board.getColumns());
                    statement.setInt(3, boardId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    conn.rollback();
                    logger.error("Nie udalo sie zaktualizowac planszy");
                    throw new DatabaseOperationException("database.error.board_update_failure", e);
                }

                // Usunięcie starych komórek
                logger.info("Usuwanie starych komorek planszy");
                try (PreparedStatement statement = conn.prepareStatement(
                        "DELETE FROM cell WHERE board_id = ?;")) {
                    statement.setInt(1, boardId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    conn.rollback();
                    logger.error("Nie udalo sie usunac starych komorek");
                    throw new DatabaseOperationException("database.error.cells_delete_failure", e);
                }
            } else {
                // Dodanie nowej planszy
                logger.info("Dodawanie nowej planszy");
                try (PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO board (name, rows, columns) VALUES (?, ?, ?);")) {
                    statement.setString(1, boardName);
                    statement.setInt(2, board.getRows());
                    statement.setInt(3, board.getColumns());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    conn.rollback();
                    logger.error("Nie udalo sie dodac nowej planszy");
                    throw new DatabaseOperationException("database.error.board_add_failure", e);
                }

                // Pobranie ID nowo dodanej planszy
                logger.info("Pobieranie indeksu dodanej planszy");
                try (PreparedStatement statement = conn.prepareStatement(
                        "SELECT id FROM board WHERE name = ?;")) {
                    statement.setString(1, boardName);
                    try (ResultSet idSet = statement.executeQuery()) {
                        if (idSet.next()) {
                            boardId = idSet.getInt(1);
                        } else {
                            conn.rollback();
                            logger.error("Plansza o podanym indeksie nie istnieje");
                            throw new DatabaseOperationException("database.error.board_id_not_exists");
                        }
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    logger.error("Nie udalo pobrac indeksu nowej planszy");
                    throw new DatabaseOperationException("database.error.board_id_failure", e);
                }
            }

            // Dodanie komórek
            logger.info("Dodawanie komorek planszy do bazy danych");
            try (PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO cell (x, y, value, board_id)"
                        + "VALUES (?, ?, ?, ?);")) {
                for (int i = 0; i < board.getRows(); i++) {
                    for (int j = 0; j < board.getColumns(); j++) {
                        statement.setInt(1, i);
                        statement.setInt(2, j);
                        statement.setBoolean(3, board.getCell(i, j).getCellValue());
                        statement.setInt(4, boardId);
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Blad podczas dodawania komorek planszy do bazy danych");
                throw new DatabaseOperationException("database.error.cells_add_failure", e);
            }

            conn.commit();
            logger.info("Pomyslnie zapisano plansze wraz z komorkami");
        } catch (SQLException e) {
            logger.error("Nie udalo sie polaczyc z baza danych. URL: {}, User: {}, Error: {}",
                    url, user, e.getMessage(), e);
            throw new DatabaseOperationException("database.error.connection_failure: " + e.getMessage(), e);
        }
    }

    public List<String> names() throws DatabaseOperationException {
        logger.info("Laczenie z baza danych");
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT name FROM board;");
             ResultSet boardsSet = statement.executeQuery()) {
            logger.info("Odczytywanie nazw plansz z bazy danych");
            List<String> names = new ArrayList<>();

            while (boardsSet.next()) {
                names.add(boardsSet.getString("name"));
            }

            return names;
        } catch (SQLException e) {
            logger.error("Nie udalo sie polaczyc z baza danych. URL: {}, User: {}, Error: {}",
                    url, user, e.getMessage(), e);
            throw new DatabaseOperationException("database.error.connection_failure: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws DatabaseOperationException {

    }
}
