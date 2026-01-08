package progkom;

public class GameOfLifeBoardDaoFactory {
    public static Dao<GameOfLifeBoard> createFileDao(String fileName) {
        return new FileGameOfLifeBoardDao(fileName);
    }

    public static Dao<GameOfLifeBoard> createJdbcDao(String boardName) {
        return new JdbcGameOfLifeBoardDao("jdbc:postgresql://localhost:5432/nbddb", "nbd", "nbdpassword", boardName);
    }
}
