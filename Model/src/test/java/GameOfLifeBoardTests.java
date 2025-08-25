import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import progkom.*;

class ToStringEqualsHashCodeTest {


    @Test
    void testGameOfLifeBoardToStringContainsRows() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertTrue(board.toString().contains("rows=2"));
    }

    @Test
    void testGameOfLifeBoardToStringContainsColumns() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertTrue(board.toString().contains("columns=3"));
    }

    @Test
    void testGameOfLifeBoardEqualsSameInstance() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertEquals(board, board);
        assertEquals(board.hashCode(), board.hashCode());

    }


    @Test
    void testGameOfLifeCellToString() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertTrue(cell.toString().contains("value=true"));
    }

    @Test
    void testGameOfLifeCellEqualsSameInstance() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertEquals(cell, cell);
    }

    @Test
    void testGameOfLifeCellHashCodeDifferentValues() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(false);
        assertNotEquals(cell1, cell2);
        assertNotEquals(cell1.hashCode(), cell2.hashCode());
    }

    @Test
    void testGameOfLifeBoardToStringContainsRows1() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertTrue(board.toString().contains("rows=2"));
    }

    @Test
    void testGameOfLifeBoardToStringContainsColumns1() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertTrue(board.toString().contains("columns=3"));
    }

    @Test
    void testGameOfLifeBoardEqualsSameInstance1() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertEquals(board, board); // Reflexivity
        assertEquals(board.hashCode(), board.hashCode());

    }

    @Test
    void testGameOfLifeCellToString1() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertTrue(cell.toString().contains("value=true"));
    }

    @Test
    void testGameOfLifeCellEqualsSameInstance1() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertEquals(cell, cell);
        assertEquals(cell.hashCode(), cell.hashCode());
    }


    @Test
    void testGameOfLifeCellHashCodeSameValue1() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(true);
        assertEquals(cell1, cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());
    }

    @Test
    void testGameOfLifeCellHashCodeDifferentValues1() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(false);
        assertNotEquals(cell1, cell2);
        assertNotEquals(cell1.hashCode(), cell2.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        assertNotEquals(cell, null, "Equals powinno zwrocic false dla null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        String other = "Not a GameOfLifeCell";
        assertNotEquals(cell, other, "Equals powinno zwrocic false dla obiektu innej klasy");
    }

    @Test
    void testEqualsWithSameClass() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(true);
        assertEquals(cell1, cell2, "Equals powinno zwrocic true dla obiektow tej samej klasy z tym samym stanem");
    }

}
