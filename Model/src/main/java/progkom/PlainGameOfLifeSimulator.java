package progkom;

import java.io.Serializable;

public class PlainGameOfLifeSimulator implements GameOfLifeSimulator, Serializable {

    public PlainGameOfLifeSimulator() {
    }

    @Override
    public void doStep(GameOfLifeBoard board) {
        int rows = board.getRows();
        int columns = board.getColumns();

        boolean[][] newStates = new boolean[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newStates[i][j] = board.getCell(i, j).nextState();
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board.getCell(i, j).updateState(newStates[i][j]);
            }
        }
    }

}
