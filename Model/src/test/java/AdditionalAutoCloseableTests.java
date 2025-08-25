import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import progkom.FileGameOfLifeBoardDao;
import progkom.FileProcessingException;
import progkom.GameOfLifeBoard;
import progkom.PlainGameOfLifeSimulator;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class AdditionalAutoCloseableTests {

    private static final Logger testLogger = LoggerFactory.getLogger(FileGameOfLifeBoardDaoTest.class);

    @Test
    void testAutoCloseableTryWithResources() {
        String testFileName = "testFileAutoCloseable.dat";
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        GameOfLifeBoard board = new GameOfLifeBoard(5, 5, simulator);

        try (FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName)) {
            dao.write(board);
            GameOfLifeBoard Board = dao.read();
            assertNotNull(Board, "Odczytana plansza nie moze byc null");
            for(int i = 0 ; i < 5; i++){
                for(int j = 0; j < 5; j++){
                    assertEquals(board.getCell(i,j).getCellValue(), Board.getCell(i,j).getCellValue(), "Odczytana plansza ma byc zgodna z zapisana.");
                }
            }
        }

        File file = new File(testFileName);
        assertTrue(file.exists(), "Plik testowy musi zostac utworzony w trakcie testu");
        assertTrue(file.delete(), "Plik testowy musi zostac usuniety po tescie");
    }

    @Test
    void testCloseWithoutTryWithResources() {
        String testFileName = "testExplicitClose.dat";
        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName);

        assertDoesNotThrow(dao::close, "Metoda close powinna dzialac bez wyjatkow przy recznym wywolaniu");

        File file = new File(testFileName);
        if (file.exists()) {
            assertTrue(file.delete(), "Plik testowy powinien zostac usuniety po tescie");
        }
    }

    @Test
    void testAutoCloseableClosesOnErrorInWrite() {
        String testFileName = "testWriteError.dat";

        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName);

        try (FileGameOfLifeBoardDao resourceDao = dao) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> resourceDao.write(null));
            assertEquals("Obiekt GameOfLifeBoard nie może być null.", exception.getMessage());
        }

        testLogger.info("Test zakonczony. Metoda close powinna zostac wywolana automatycznie w trywihtresources");
    }

    @Test
    void testAutoCloseableClosesOnErrorInRead() {
        String testFileName = "testReadError.dat";

        File invalidFile = new File(testFileName);
        try {
            assertFalse(invalidFile.createNewFile(), "Nie udalo sie utworzyc pliku");
        } catch (Exception e) {
            fail("Nieoczekiwany wyjatek podczas tworzenia pliku testowego: " + e.getMessage());
        }

        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName);
        try (FileGameOfLifeBoardDao resourceDao = dao) {
            Exception exception = assertThrows(FileProcessingException.class, resourceDao::read);
            assertNotNull(exception.getMessage(), "Wyjatek zawiera komunikat bledu");
        }

        testLogger.info("Metoda close zostala automatycznie wywolana, mimo ze wystapil blad odczytu");

        assertFalse(invalidFile.delete(), "Plik testowy nie zostal usuniety.");
    }

    @Test
    void testManualCloseCalledAfterOperations() {
        String testFileName = "testManualClose.dat";
        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName);
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        GameOfLifeBoard board = new GameOfLifeBoard(4, 4, simulator);

        dao.write(board);
        GameOfLifeBoard readBoard = dao.read();
        assertNotNull(readBoard, "Odczytana plansza nie moze byc null.");
        for(int i = 0 ; i < 4; i++){
            for(int j = 0; j < 4; j++){
                assertEquals(board.getCell(i,j).getCellValue(), readBoard.getCell(i,j).getCellValue(), "Odczytana plansza ma byc zgodna z zapisana.");
            }
        }
        assertDoesNotThrow(dao::close, "Metoda close nie powinna powodowac wyjatkow");

        File file = new File(testFileName);
        assertTrue(file.delete(), "Plik testowy powinien byc usuniety");
    }
}