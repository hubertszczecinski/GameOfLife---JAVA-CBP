package progkom;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOfLifeColumn extends GameOfLifeCellBase implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameOfLifeColumn(List<GameOfLifeCell> cells) {
        super(cells);
    }

    @Override
    public GameOfLifeColumn clone() {
        List<GameOfLifeCell> newCells = new ArrayList<>();
        for (GameOfLifeCell cell : this.getCells()) {
            newCells.add(cell.clone());
        }
        return new GameOfLifeColumn(Collections.unmodifiableList(newCells));
    }
}
