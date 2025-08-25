import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import progkom.FileGameOfLifeBoardDao;
import progkom.FileProcessingException;
import progkom.GameOfLifeBoard;
import progkom.PlainGameOfLifeSimulator;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DaoDodatkoweTests {

    private FileGameOfLifeBoardDao dao;
    private File tempFile;
    private GameOfLifeBoard board;
    private PlainGameOfLifeSimulator simulator;

    @BeforeEach
    public void setUp() throws IOException {

        tempFile = File.createTempFile("testBoard", ".dat");
        tempFile.deleteOnExit();
        dao = new FileGameOfLifeBoardDao(tempFile.getAbsolutePath());

        board = new GameOfLifeBoard(5, 5,simulator);
    }

    @Test
    public void testWriteMethod() throws IOException {

        dao.write(board);


        assertTrue(tempFile.exists(), "Plik powinien istnieć po zapisaniu");


        assertTrue(tempFile.length() > 0, "Plik nie może być pusty po zapisaniu obiektu");
    }

    @Test
    public void testReadMethod() throws IOException {

        dao.write(board);


        GameOfLifeBoard readBoard = dao.read();


        assertNotNull(readBoard, "Odczytany obiekt nie może być null");
        assertEquals(board, readBoard, "Odczytany obiekt powinien być równy zapisanym danym");
    }

    @Test
    public void testWriteThenRead() throws IOException {

        dao.write(board);


        GameOfLifeBoard readBoard = dao.read();


        assertEquals(board, readBoard, "Obiekt zapisany i odczytany z pliku powinny być równe");
    }

    @Test
    public void testFileDeletionAfterTest() throws IOException {

        File testFile = new File(tempFile.getAbsolutePath());
        assertTrue(testFile.exists(), "Plik powinien istnieć przed zakończeniem testu");
    }
    @Test
    public void testWriteCreatesFile() {
        GameOfLifeBoard board = new GameOfLifeBoard(5, 5, simulator);


        dao.write(board);


        assertTrue(tempFile.exists(), "Plik powinien istnieć po zapisaniu");
        assertTrue(tempFile.length() > 0, "Plik nie może być pusty po zapisaniu obiektu");
    }

    @Test
    public void testWriteThrowsRuntimeException() {

        File invalidFile = new File("/invalid_path/test.dat");
        FileGameOfLifeBoardDao invalidDao = new FileGameOfLifeBoardDao(invalidFile.getAbsolutePath());

        GameOfLifeBoard board = new GameOfLifeBoard(5, 5,simulator);


        FileProcessingException exception = assertThrows(FileProcessingException.class,
                () -> invalidDao.write(board),
                "Powinien być rzucony RuntimeException w przypadku błędu zapisu");
        assertFalse(exception.getMessage().contains("Nie mozna zapisac pliku."));
    }
}
