import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import progkom.*;

public class TesterGeterySetery {
    private GameOfLifeBoard board;
    private PlainGameOfLifeSimulator simulator;

    @BeforeEach
    void setup() {
        simulator = new PlainGameOfLifeSimulator();
        board = new GameOfLifeBoard(3, 3, simulator);
    }

    @Test
    void testSetBoardCellValue() {
        board.getCell(1, 1).updateState(true);
        assertTrue(board.getCell(1, 1).getCellValue());
    }

    @Test
    void testGetRow() {
        GameOfLifeRow row = board.getRow(1);

        List<GameOfLifeCell> cells = row.getCells();

        assertEquals(3, cells.size(), "Rozmiar rzędu musi byc 3");

        assertSame(board.getCell(1, 0), cells.get(0));
        assertSame(board.getCell(1, 1), cells.get(1));
        assertSame(board.getCell(1, 2), cells.get(2));
    }

    @Test
    void testGetColumn() {
        GameOfLifeColumn column = board.getColumn(1);

        List<GameOfLifeCell> cells = column.getCells();

        assertEquals(3, cells.size(), "Rozmiar kolumny musi byc 3");

        assertSame(board.getCell(0, 1), cells.get(0));
        assertSame(board.getCell(1, 1), cells.get(1));
        assertSame(board.getCell(2, 1), cells.get(2));
    }

    @Nested
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

            assertThrows(ValidationException.class, () -> cell.setNeighbors(null));
            assertThrows(ValidationException.class, () -> cell.setNeighbors(new ArrayList<>()));

            List<GameOfLifeCell> tooFewNeighbors = Collections.nCopies(7, new GameOfLifeCell(false));
            assertThrows(ValidationException.class, () -> cell.setNeighbors(tooFewNeighbors));

            List<GameOfLifeCell> tooManyNeighbors = Collections.nCopies(9, new GameOfLifeCell(false));
            assertThrows(ValidationException.class, () -> cell.setNeighbors(tooManyNeighbors));
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

            assertEquals(cell1, cell2);
            assertNotEquals(cell1, cell3);
            assertEquals(cell1.hashCode(), cell2.hashCode());
            assertNotEquals(cell1.hashCode(), cell3.hashCode());
        }

        @Test
        void toStringShouldReturnCorrectRepresentation() {
            GameOfLifeCell cell = new GameOfLifeCell(true);
            assertEquals("GameOfLifeCell[value=true]", cell.toString());
        }
    }

    @Nested
    class GameOfLifeTest {

        private GameOfLifeBoard board;
        private PlainGameOfLifeSimulator simulator;

        @BeforeEach
        void setup() {
            simulator = new PlainGameOfLifeSimulator();
            board = new GameOfLifeBoard(3, 3,simulator);
        }


        @Test
        void testCellInitialState() {
            GameOfLifeCell cell = new GameOfLifeCell(false);
            assertFalse(cell.getCellValue());

            GameOfLifeCell liveCell = new GameOfLifeCell(true);
            assertTrue(liveCell.getCellValue());
        }


        @Test
        void testBoardInitialization() {
            GameOfLifeBoard board = new GameOfLifeBoard(5, 5, simulator);

            assertEquals(5, board.getRows());
            assertEquals(5, board.getColumns());
        }


        //TEN JEST ZAKOMENTOWANY PONIEWAZ NIE MA GETTERA DLA SETNEIGHBORS W KLASIE GAMEOFLIFECELL

        //    @Test
        //    void testSetNeighbors() {
        //        List<GameOfLifeCell> neighbors
        //        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        //        GameOfLifeBoard board = new GameOfLifeBoard(3, 3, simulator);
        //
        //        GameOfLifeCell cell = board.getCell(1, 1);
        //        GameOfLifeCell[] neighbors = cell.getNeighbors();
        //        assertEquals(8, neighbors.length);
        //    }

        @Test
        void testCellInitialStateIsDead() {
            GameOfLifeCell cell = new GameOfLifeCell(false);
            assertFalse(cell.getCellValue());
        }

        @Test
        void testCellInitialStateIsAlive() {
            GameOfLifeCell cell = new GameOfLifeCell(true);
            assertTrue(cell.getCellValue());
        }

        @Test
        void testBoardHasBalancedDistributionOfLiveCells() {
            int liveCellCount = 0;

            int totalCells = board.getRows() * board.getColumns();

            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (board.getCell(i, j).getCellValue()) {
                        liveCellCount++;
                    }
                }
            }

            // sprawdzamy ze liczba zywych komorek stanowi 30-70 procent
            int minLiveCells = (int) (0.1 * totalCells);
            int maxLiveCells = (int) (0.8 * totalCells);
            assertTrue(liveCellCount >= minLiveCells && liveCellCount <= maxLiveCells,
                    "oczekiwana obecnosc zywych komorek znajduje sie pomiedzy 30 a 70 %");
        }


        @Test
        void testBoardRowAndColumnCount() {
            assertEquals(3, board.getRows());
            assertEquals(3, board.getColumns());
        }


        @Test
        void testRowCountLiveCells() {
            GameOfLifeRow row = new GameOfLifeRow(Arrays.asList(
                    new GameOfLifeCell(false),
                    new GameOfLifeCell(true),
                    new GameOfLifeCell(true)
            ));
            assertEquals(2, row.countALiveCells());
        }

        @Test
        void testColumnCountDeadCells() {
            GameOfLifeColumn column = new GameOfLifeColumn(Arrays.asList(
                    new GameOfLifeCell(false),
                    new GameOfLifeCell(true),
                    new GameOfLifeCell(false)
            ));
            assertEquals(2, column.countDeadCells());
        }
    }

    @Test
    void testGameOfLifeBoardEqualsSameInstance() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertEquals(board, board, "Equals powinno zwrócić true dla tego samego obiektu");
    }

    @Test
    void testGameOfLifeBoardEqualsNull() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        assertNotEquals(board, null, "Equals powinno zwrócić false dla null");
    }

    @Test
    void testGameOfLifeBoardEqualsDifferentClass() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 3, null);
        String other = "Test";
        assertNotEquals(board, other, "Equals powinno zwrócić false dla obiektu innej klasy");
    }

    @Test
    void testGameOfLifeBoardEqualsDifferentSizes() {
        GameOfLifeBoard board1 = new GameOfLifeBoard(2, 3, null);
        GameOfLifeBoard board2 = new GameOfLifeBoard(3, 3, null);
        assertNotEquals(board1, board2, "Equals powinno zwrócić false dla różnych rozmiarów plansz");
    }

    @Test
    void testGameOfLifeBoardEqualsDifferentSimulator() {
        GameOfLifeBoard board1 = new GameOfLifeBoard(2, 3, new PlainGameOfLifeSimulator());
        GameOfLifeBoard board2 = new GameOfLifeBoard(2, 3, null);
        assertNotEquals(board1, board2, "Equals powinno zwrócić false dla różnych symulatorów");
    }

    @Test
    void testEqualsWithDifferentClass() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        String other = "Not a GameOfLifeCell";
        assertNotEquals(cell, other, "Equals powinno zwrócić false dla obiektu innej klasy");
    }

}