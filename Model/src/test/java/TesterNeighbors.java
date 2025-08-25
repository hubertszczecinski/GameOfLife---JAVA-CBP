import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import progkom.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TesterNeighbors {
    private GameOfLifeBoard board;
    private PlainGameOfLifeSimulator simulator;

    @BeforeEach
    void setup() {
        simulator = new PlainGameOfLifeSimulator();
        board = new GameOfLifeBoard(3, 3, simulator);
    }

    @Test
    void testCellNextStateAliveWithTwoOrThreeNeighbors() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8,null)) ;
        neighbors.set(0, new GameOfLifeCell(true));
        neighbors.set(1, new GameOfLifeCell(true));
        cell.setNeighbors(neighbors);

        assertTrue(cell.nextState());
    }

    @Test
    void testCellDiesWithLessThanTwoNeighbors() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8,null));
        neighbors.set(0,new GameOfLifeCell(true));

        cell.setNeighbors(neighbors);

        assertFalse(cell.nextState());
    }

    @Test
    void testCellBornsWithExactlyThreeNeighbors() {
        GameOfLifeCell cell = new GameOfLifeCell(false);
        List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8,null));
        neighbors.set(0,new GameOfLifeCell(true));
        neighbors.set(1,new GameOfLifeCell(true));
        neighbors.set(2,new GameOfLifeCell(true));

        cell.setNeighbors(neighbors);

        assertTrue(cell.nextState());
    }

    @Test
    void testCellWithTwoLiveNeighborsSurvives() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8,null));
        neighbors.set(0,new GameOfLifeCell(true));
        neighbors.set(1,new GameOfLifeCell(true));
        neighbors.set(2,new GameOfLifeCell(false));
        neighbors.set(3,new GameOfLifeCell(false));
        neighbors.set(4,new GameOfLifeCell(false));
        neighbors.set(5,new GameOfLifeCell(false));
        neighbors.set(6,new GameOfLifeCell(false));
        neighbors.set(7,new GameOfLifeCell(false));

        cell.setNeighbors(neighbors);

        assertTrue(cell.nextState());
    }

    @Test
    void testCellWithMoreThanThreeLiveNeighborsDies() {
        GameOfLifeCell cell = new GameOfLifeCell(true);
        List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8,null));
        neighbors.set(0,new GameOfLifeCell(true));
        neighbors.set(1,new GameOfLifeCell(true));
        neighbors.set(2,new GameOfLifeCell(true));
        neighbors.set(3,new GameOfLifeCell(true));
        neighbors.set(4,new GameOfLifeCell(false));
        neighbors.set(5,new GameOfLifeCell(false));
        neighbors.set(6,new GameOfLifeCell(false));
        neighbors.set(7,new GameOfLifeCell(false));

        cell.setNeighbors(neighbors);

        assertFalse(cell.nextState());
    }

    @Test
    void testCellWithExactlyThreeLiveNeighborsIsBorn() {
        GameOfLifeCell cell = new GameOfLifeCell(false);

        List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8,null));
        neighbors.set(0,new GameOfLifeCell(true));
        neighbors.set(1,new GameOfLifeCell(true));
        neighbors.set(2,new GameOfLifeCell(true));
        neighbors.set(3,new GameOfLifeCell(false));
        neighbors.set(4,new GameOfLifeCell(false));
        neighbors.set(5,new GameOfLifeCell(false));
        neighbors.set(6,new GameOfLifeCell(false));
        neighbors.set(7,new GameOfLifeCell(false));

        cell.setNeighbors(neighbors);

        assertTrue(cell.nextState());
    }
}