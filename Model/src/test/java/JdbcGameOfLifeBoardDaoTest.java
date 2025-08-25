import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import progkom.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcGameOfLifeBoardDaoTest {
    @BeforeEach
    public void setUp() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gol", "nbd", "nbdpassword");
             PreparedStatement statement = conn.prepareStatement(
                     "TRUNCATE TABLE board, cell;")) {
            statement.executeUpdate();
        }
    }

    @Test
    public void namesTest() {
        String testName = "NamesTest";

        try (JdbcGameOfLifeBoardDao dao = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao(testName)) {
            GameOfLifeBoard board = new GameOfLifeBoard(5, 5, new PlainGameOfLifeSimulator());
            assertDoesNotThrow(() -> dao.write(board));
            assertDoesNotThrow(dao::names);
            List<String> list = dao.names();
            assertEquals(1, list.size());
            assertEquals(testName, list.getFirst());
        }
    }

    @Test
    public void writeReadTest() {
        String testName = "WriteReadTest";

        try (JdbcGameOfLifeBoardDao dao = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao(testName)) {
            GameOfLifeBoard board = new GameOfLifeBoard(6, 6, new PlainGameOfLifeSimulator());
            assertDoesNotThrow(() -> dao.write(board));
            assertDoesNotThrow(dao::read);
            GameOfLifeBoard readBoard = dao.read();

            assertEquals(readBoard.getRows(), board.getRows());
            assertEquals(readBoard.getColumns(), board.getColumns());
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    assertEquals(readBoard.getCell(i, j).getCellValue(), board.getCell(i, j).getCellValue());
                    assertNotSame(readBoard.getCell(i, j), board.getCell(i, j));

                }
            }
            assertNotSame(readBoard, board);
        }
    }

    @Test
    public void exceptionsTest() throws Exception {
        try (JdbcGameOfLifeBoardDao dao = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao("ExceptionsTest")) {
            GameOfLifeBoard board1 = new GameOfLifeBoard(3, 3, new PlainGameOfLifeSimulator());
            dao.write(board1);

            try (JdbcGameOfLifeBoardDao dao2 = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao("ExceptionsTest")) {
                assertThrows(DatabaseOperationException.class, () -> dao2.write(board1));
            }

            setUp();
            assertThrows(DatabaseOperationException.class, dao::read);
        }
    }

    @Test
    public void testTransactionManagement() {
        String testName = "TransactionManagementTest";

        try (JdbcGameOfLifeBoardDao dao = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao(testName)) {
            GameOfLifeBoard board = new GameOfLifeBoard(4, 4, new PlainGameOfLifeSimulator());

            // Pierwszy zapis ma sie udac
            dao.write(board);

            // Podczas proby zapisu planszy o tej samej nazwie ma byc rzucony wyjatek
            assertThrows(DatabaseOperationException.class, () -> dao.write(board));

            // Sprawdzamy czy pierwszy zapis zostal zatwierdzony
            List<String> names = dao.names();
            assertTrue(names.contains(testName), "Pierwszy zapis powinien być zatwierdzony.");

            // Sprawdzamy ze druga transakcja zostala wycofana
            assertEquals(1, names.size(), "Tylko jedna plansza powinna istnieć w bazie.");
        }
    }

    @Test
    public void testResourceClosing() {
        String testName = "ResourceClosingTest";

        JdbcGameOfLifeBoardDao dao = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao(testName);
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3, new PlainGameOfLifeSimulator());
        dao.write(board);

        assertDoesNotThrow(dao::close);
    }

    @Test
    public void testResourceClosingWithClose() throws DatabaseOperationException {
        try (JdbcGameOfLifeBoardDao dao = (JdbcGameOfLifeBoardDao) GameOfLifeBoardDaoFactory.createJdbcDao("ResourceClosingTest")) {
            GameOfLifeBoard board = new GameOfLifeBoard(3, 3, new PlainGameOfLifeSimulator());
            dao.write(board);

            dao.close();

            assertDoesNotThrow(dao::close, "Metoda close() nie powinna rzucac wyjatkow");
        }
    }
}
