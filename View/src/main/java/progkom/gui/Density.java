package progkom.gui;

import progkom.GameOfLifeBoard;

import java.util.Random;

public enum Density {
    LOW(10),
    MEDIUM(30),
    HIGH(50);

    private final int percentage;

    Density(int percentage) {
        this.percentage = percentage;
    }

    public void applyDensity(GameOfLifeBoard board) {
        Random random = new Random();

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                boolean isAlive = random.nextInt(100) < percentage;
                board.getCell(i, j).updateState(isAlive);
            }
        }
    }
}
