import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import progkom.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameOfLifeBoardTest {
    private GameOfLifeBoard board;
    PlainGameOfLifeSimulator simulator;
    @BeforeEach
    void setUp() {
        List<List<GameOfLifeCell>> cells = Arrays.asList(
                Arrays.asList(new GameOfLifeCell(false), new GameOfLifeCell(true)),
                Arrays.asList(new GameOfLifeCell(true), new GameOfLifeCell(false))
        );
        board = new GameOfLifeBoard(cells.size(), cells.get(0).size(), simulator);
    }

    @Test
    void testGetRow_ValidIndex() {
        GameOfLifeRow row = board.getRow(1);
        assertNotNull(row);
        assertEquals(2, row.getCells().size());
    }

    @Test
    void testGetRow_InvalidIndexNegative() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> board.getRow(-1)
        );
        assertEquals("Nieprawidlowy indeks wiersza", exception.getMessage());
    }

    @Test
    void testGetRow_InvalidIndexOutOfBounds() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> board.getRow(3)
        );
        assertEquals("Nieprawidlowy indeks wiersza", exception.getMessage());
    }


    @Test
    void testGetColumn_InvalidIndexNegative() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> board.getColumn(-1)
        );
        assertEquals("Nieprawidlowy indeks kolumny", exception.getMessage());
    }

    @Test
    void testGetColumn_InvalidIndexOutOfBounds() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> board.getColumn(3)
        );
        assertEquals("Nieprawidlowy indeks kolumny", exception.getMessage());
    }

    @Test
    void testCloneCreatesDeepCopy() {
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        GameOfLifeBoard original = new GameOfLifeBoard(2, 2, simulator);

        GameOfLifeBoard clone = original.clone();

        assertNotSame(original, clone);
        assertEquals(original, clone);
        for (int i = 0; i < original.getRows(); i++) {
            for (int j = 0; j < original.getColumns(); j++) {
                assertNotSame(original.getCell(i, j), clone.getCell(i, j));
            }
        }
    }

    @Test
    void clearTest() {
        board.clear();
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                assertFalse(board.getCell(i, j).getCellValue());
            }
        }
    }

    @Test
    void missingBranchesTests() {
        assertThrows(ValidationException.class, () -> {
            GameOfLifeBoard wrongBoard = new GameOfLifeBoard(-1, -1, new PlainGameOfLifeSimulator());
        });
        assertThrows(ValidationException.class, () -> board.setCellValue(-1, -1, true));
        assertThrows(ValidationException.class, () -> board.getCell(-1, -1));
    }

}
