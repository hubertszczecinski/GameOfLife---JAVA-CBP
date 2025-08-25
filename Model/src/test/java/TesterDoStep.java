import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import progkom.*;
public class TesterDoStep {
    private GameOfLifeBoard board;

    @BeforeEach
    void setup() {
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        board = new GameOfLifeBoard(3, 3, simulator);
    }

    @Test
    void testDoSimulationStep_NoSimulator() {
        GameOfLifeBoard boardWithoutSimulator = new GameOfLifeBoard(3, 3, null);

        assertDoesNotThrow(boardWithoutSimulator::doSimulationStep,
                "doSimulationStep ma dzialac dobzre tylko wtedy gdy simulator jest null");
    }

    @Test
    void testDoSimulationStep() {
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3, simulator);

        // Ustawiamy poczatkowy stan komorek
        board.getCell(0, 1).updateState(true);
        board.getCell(1, 0).updateState(true);
        board.getCell(1, 1).updateState(true);
        board.getCell(1, 2).updateState(true);
        board.getCell(2, 1).updateState(true);

        // Wykonanie kroku symulacji
        board.doSimulationStep();

        assertFalse(board.getCell(0, 1).getCellValue(), "Komorka (0,1) powina byc martwa");
    }

    @Test
    void testDoStepUpdatesCellStatesCorrectly() {
        PlainGameOfLifeSimulator simulator = new PlainGameOfLifeSimulator();
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3, simulator);


        board.getCell(0, 1).updateState(true);
        board.getCell(1, 2).updateState(true);
        board.getCell(2, 0).updateState(true);
        board.getCell(2, 1).updateState(true);
        board.getCell(2, 2).updateState(true);

        simulator.doStep(board);


        assertFalse(board.getCell(0, 1).getCellValue());
        assertFalse(board.getCell(1, 2).getCellValue());
        assertFalse(board.getCell(2, 1).getCellValue());
    }

}