import org.junit.jupiter.api.Test;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

import progkom.*;

class FileGameOfLifeBoardDaoTest {

    @Test
    void testTryWithResources() throws Exception {
        String testFileName = "test_resources.dat";
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3, simulator);

        try (FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName)) {
            dao.write(board);
        }

        try (FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao(testFileName)) {
            GameOfLifeBoard readBoard = dao.read();
            assertNotNull(readBoard, "Obiekt odczytany nie powinien być null");
        }

        new File(testFileName).delete();
    }

    @Test
    public void testCreateFileDao() {
        String fileName = "testFileDao.dat";

        Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.createFileDao(fileName);

        assertNotNull(dao, "Fabryka powinna zwrócić instancję DAO");
        assertTrue(dao instanceof FileGameOfLifeBoardDao, "DAO powinno być instancją FileGameOfLifeBoardDao");

        File file = new File(fileName);
        assertEquals(fileName, ((FileGameOfLifeBoardDao) dao).getFileName(),
                "Fabryka powinna przekazać poprawną nazwę pliku do FileGameOfLifeBoardDao");
    }

    @Test
    public void testClassIsInstantiable() {
        GameOfLifeBoardDaoFactory factory = new GameOfLifeBoardDaoFactory();
        assertNotNull(factory, "Instancja klasy GameOfLifeBoardDaoFactory nie powinna być null");
    }

    @Test
    public void testCreateFileDaoCoverage() {
        Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.createFileDao("testFile.dat");
        assertNotNull(dao, "Metoda createFileDao powinna zwrócić instancję DAO");
        assertTrue(dao instanceof FileGameOfLifeBoardDao, "createFileDao powinna zwrócić FileGameOfLifeBoardDao");
    }

    @Test
    void testConstructorThrowsExceptionForNullFileName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new FileGameOfLifeBoardDao(null));
        assertEquals("Nazwa pliku nie może być pusta.", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionForEmptyFileName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new FileGameOfLifeBoardDao(""));
        assertEquals("Nazwa pliku nie może być pusta.", exception.getMessage());
    }

    @Test
    void testWriteThrowsExceptionForNullBoard() {
        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao("testFile.dat");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> dao.write(null));
        assertEquals("Obiekt GameOfLifeBoard nie może być null.", exception.getMessage());
    }

    @Test
    void testReadReturnsNullForNonExistingFile() {
        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao("nonExistingFile.dat");
        GameOfLifeBoard result = dao.read();

        assertNull(result, "Metoda read powinna zwrócić null, jeśli plik nie istnieje");
    }

    @Test
    void testCloseMethodDoesNotThrowAnyException() {
        FileGameOfLifeBoardDao dao = new FileGameOfLifeBoardDao("testFile.dat");
        assertDoesNotThrow(dao::close, "Zamykanie obiektu DAO nie powinno rzucać żadnych wyjątków");
    }
}