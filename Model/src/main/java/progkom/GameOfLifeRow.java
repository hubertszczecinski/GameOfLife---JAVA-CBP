package progkom;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOfLifeRow extends GameOfLifeCellBase implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameOfLifeRow(List<GameOfLifeCell> cells) {
        super(cells);
    }

    @Override
    public GameOfLifeRow clone() {
        List<GameOfLifeCell> newCells = new ArrayList<>();
        for (GameOfLifeCell cell : this.getCells()) {
            newCells.add(cell.clone());
        }
        return new GameOfLifeRow(Collections.unmodifiableList(newCells));
    }
}
