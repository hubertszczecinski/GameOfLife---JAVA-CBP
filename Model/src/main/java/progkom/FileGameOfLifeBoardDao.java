package progkom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileGameOfLifeBoardDao implements Dao<GameOfLifeBoard>, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(FileGameOfLifeBoardDao.class);

    private final String fileName;

    public FileGameOfLifeBoardDao(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Nazwa pliku nie może być pusta.");
        }
        this.fileName = fileName;
        logger.info("Utworzono FileGameOfLifeBoardDao z plikiem: {}", fileName);
    }

    @Override
    public GameOfLifeBoard read() {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.warn("Plik {} nie istnieje. Zwracamy null.", fileName);
            return null;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            logger.info("Odczytano obiekt z pliku: {}", fileName);
            return (GameOfLifeBoard) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Nie mozna odczytac pliku: {}", fileName, e);
            throw new FileProcessingException("file.error.read");
        }
    }

    @Override
    public void write(GameOfLifeBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Obiekt GameOfLifeBoard nie może być null.");
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(board);
            logger.info("Zapisano obiekt do pliku: {}", fileName);
        } catch (IOException e) {
            logger.error("Nie mozna zapisac pliku {}", fileName, e);
            throw new FileProcessingException("file.error.write");
        }
    }

    @Override
    public void close() {
        logger.info("Zamknięto FileGameOfLifeBoardDao dla pliku: {}", fileName);
    }

    public String getFileName() {
        return fileName;
    }
}
