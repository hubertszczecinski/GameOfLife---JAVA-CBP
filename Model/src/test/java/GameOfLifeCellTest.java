import javafx.scene.control.Cell;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import progkom.*;

class GameOfLifeCellTest {

    @Test
    void constructorShouldInitializeWithGivenValue() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertTrue(cell.getCellValue());

        GameOfLifeCell anotherCell = new GameOfLifeCell(false);
        assertFalse(anotherCell.getCellValue());
    }

    @Test
    void setNeighborsShouldThrowExceptionForInvalidList() {
        GameOfLifeCell cell = new GameOfLifeCell(false);

        ValidationException ex1 = assertThrows(ValidationException.class, () -> cell.setNeighbors(null));
        assertEquals("Lista sasiadow nie moze byc null.", ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class, () -> cell.setNeighbors(new ArrayList<>()));
        assertEquals("Lista sasiadow musi zawierac dokladnie 8 elementow. Podano: {0}.", ex2.getMessage());

        List<GameOfLifeCell> tooFewNeighbors = Collections.nCopies(7, new GameOfLifeCell(false));
        ValidationException ex3 = assertThrows(ValidationException.class, () -> cell.setNeighbors(tooFewNeighbors));
        assertEquals("Lista sasiadow musi zawierac dokladnie 8 elementow. Podano: {0}.", ex3.getMessage());

        List<GameOfLifeCell> tooManyNeighbors = Collections.nCopies(9, new GameOfLifeCell(false));
        ValidationException ex4 = assertThrows(ValidationException.class, () -> cell.setNeighbors(tooManyNeighbors));
        assertEquals("Lista sasiadow musi zawierac dokladnie 8 elementow. Podano: {0}.", ex4.getMessage());
    }

    @Test
    void setNeighborsShouldAcceptValidList() {
        GameOfLifeCell cell = new GameOfLifeCell(false);
        List<GameOfLifeCell> validNeighbors = Collections.nCopies(8, new GameOfLifeCell(false));

        assertDoesNotThrow(() -> cell.setNeighbors(validNeighbors));
    }

    @Test
    void nextStateShouldReturnCorrectValueForAliveCell() {
        GameOfLifeCell cell = new GameOfLifeCell(true);

        // 2 live neighbors
        List<GameOfLifeCell> neighbors = List.of(
                new GameOfLifeCell(true),
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false)
        );
        cell.setNeighbors(neighbors);
        assertTrue(cell.nextState());

        // 3 live neighbors
        neighbors = List.of(
                new GameOfLifeCell(true),
                new GameOfLifeCell(true),
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false)
        );
        cell.setNeighbors(neighbors);
        assertTrue(cell.nextState());

        // 1 live neighbor
        neighbors = List.of(
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false)
        );
        cell.setNeighbors(neighbors);
        assertFalse(cell.nextState());
    }

    @Test
    void nextStateShouldReturnCorrectValueForDeadCell() {
        GameOfLifeCell cell = new GameOfLifeCell(false);

        // 3 live neighbors
        List<GameOfLifeCell> neighbors = List.of(
                new GameOfLifeCell(true),
                new GameOfLifeCell(true),
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false)
        );
        cell.setNeighbors(neighbors);
        assertTrue(cell.nextState());

        // 2 live neighbors
        neighbors = List.of(
                new GameOfLifeCell(true),
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false),
                new GameOfLifeCell(false)
        );
        cell.setNeighbors(neighbors);
        assertFalse(cell.nextState());
    }

    @Test
    void updateStateShouldUpdateCellValue() {
        GameOfLifeCell cell = new GameOfLifeCell(false);
        assertFalse(cell.getCellValue());

        cell.updateState(true);
        assertTrue(cell.getCellValue());
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(true);
        GameOfLifeCell cell3 = new GameOfLifeCell(false);

        assertNotEquals(cell1, cell3);
        assertNotEquals(cell1.hashCode(), cell3.hashCode());
    }

    @Test
    void toStringShouldReturnCorrectRepresentation() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertEquals("GameOfLifeCell[value=true]", cell.toString());
    }

    @Test
    void testCloneCreatesDeepCopy() {
        GameOfLifeCell original = new GameOfLifeCell(true);
        GameOfLifeCell neighbor = new GameOfLifeCell(false);
        original.setNeighbors(List.of(neighbor, neighbor, neighbor, neighbor, neighbor, neighbor, neighbor, neighbor));

        GameOfLifeCell clone = original.clone();

        assertNotSame(original, clone);
        assertEquals(original, clone);
    }

    @Test
    void testCompareTo() {
        GameOfLifeCell cellTrue = new GameOfLifeCell(true);
        GameOfLifeCell cellFalse = new GameOfLifeCell(false);

        assertTrue(cellTrue.compareTo(cellFalse) > 0);
        assertTrue(cellFalse.compareTo(cellTrue) < 0);
        assertEquals(0, cellTrue.compareTo(new GameOfLifeCell(true)));
    }

    @Test
    void testCompareToThrowsNullPointerException() {
        GameOfLifeCell cell = new GameOfLifeCell(true);

        ValidationException ex1 = assertThrows(ValidationException.class, () -> cell.compareTo(null));
        assertEquals("Argument jest null...", ex1.getMessage());
    }
}