package progkom;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

//import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameOfLifeBoard implements Serializable, Cloneable, Prototype<GameOfLifeBoard> {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GameOfLifeBoard.class.getName());

    private List<List<GameOfLifeCell>> board;
    private final int rows;
    private final int columns;
    private final PlainGameOfLifeSimulator simulator;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public GameOfLifeBoard(int rows, int columns, PlainGameOfLifeSimulator simulator) {
        if (rows <= 0 || columns <= 0) {
            LOGGER.log(Level.SEVERE,
                    "Liczba wierszy ({0}) i kolumn ({1}) musi byc dodatnia", new Object[]{rows, columns});
            throw new ValidationException("validation.error.invalid_board_size");
        }
        this.rows = rows;
        this.columns = columns;
        this.simulator = simulator;

        board = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            List<GameOfLifeCell> row = new ArrayList<>(columns);
            for (int j = 0; j < columns; j++) {
                row.add(new GameOfLifeCell(false));
            }
            board.add(Collections.unmodifiableList(row));
        }

        // inicjalizuja komorki i przypisujw sasiadow
        initializeCells();
        setNeighbors();
    }

    //    public void addPropertyChangeListener(PropertyChangeListener listener) {
    //        support.addPropertyChangeListener(listener);
    //    }
    //
    //    public void removePropertyChangeListener(PropertyChangeListener listener) {
    //        support.removePropertyChangeListener(listener);
    //    }
    //
    //    public boolean getCellValue(int x, int y) {
    //        return board.get(x).get(y).getCellValue();
    //    }

    public void setCellValue(int row, int column, boolean value) {
        if (!validPosition(row, column)) {
            LOGGER.log(Level.SEVERE, "Nieprawidlowa pozycja: wiersz={0}, kolumna={1}", new Object[]{row, column});
            throw new ValidationException("validation.error.invalid_cell_position");
        }
        board.get(row).get(column).setValue(value);
        support.firePropertyChange("cellValue", null, value);
    }

    private boolean validPosition(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    private void initializeCells() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                boolean initialState = rand.nextBoolean(); // ustawiamy losowy stan poczatkowy
                board.get(i).get(j).updateState(initialState);
            }
        }
    }

    public void setNeighbors() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                List<GameOfLifeCell> neighbors = new ArrayList<>(Collections.nCopies(8, null));
                neighbors.set(0, board.get((i - 1 + rows) % rows).get((j - 1 + columns) % columns)); // gora-lewo
                neighbors.set(1, board.get((i - 1 + rows) % rows).get(j));                          // gora
                neighbors.set(2, board.get((i - 1 + rows) % rows).get((j + 1) % columns));         // gora-prawo
                neighbors.set(3, board.get(i).get((j - 1 + columns) % columns));                   // lewo
                neighbors.set(4, board.get(i).get((j + 1) % columns));                             // prawo
                neighbors.set(5, board.get((i + 1) % rows).get((j - 1 + columns) % columns));      // dol-lewo
                neighbors.set(6, board.get((i + 1) % rows).get(j));                                // dol
                neighbors.set(7, board.get((i + 1) % rows).get((j + 1) % columns));               // dol-prawo

                board.get(i).get(j).setNeighbors(neighbors);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public GameOfLifeCell getCell(int x, int y) {
        if (!validPosition(x, y)) {
            LOGGER.log(Level.SEVERE, "Nieprawidlowa pozycja: wiersz={0}, kolumna={1}", new Object[]{x, y});
            throw new ValidationException("validation.error.invalid_cell_position");
        }
        return board.get(x).get(y);
    }

    public void doSimulationStep() {
        if (simulator != null) {
            GameOfLifeBoard previousState = this.clone();

            simulator.doStep(this);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    boolean oldValue = previousState.getCell(i, j).getCellValue();
                    boolean newValue = this.getCell(i, j).getCellValue();

                    if (oldValue != newValue) {
                        support.firePropertyChange("cellState", oldValue, newValue);
                    }
                }
            }
        }
    }

    //    public void copy(GameOfLifeBoard otherBoard) {
    //        if (this.rows != otherBoard.rows || this.columns != otherBoard.columns) {
    //            LOGGER.log(Level.SEVERE, "Rozmiary plansz musza byc takie same, aby dokonac kopiowania.");
    //            throw new ValidationException("validation.error.board_size_mismatch");
    //        }
    //
    //        for (int i = 0; i < this.rows; i++) {
    //            for (int j = 0; j < this.columns; j++) {
    //                boolean newState = otherBoard.getCell(i, j).getCellValue();
    //                this.getCell(i, j).updateState(newState);
    //            }
    //        }
    //    }

    public GameOfLifeRow getRow(int y) {
        if (y < 0 || y >= rows) {
            LOGGER.log(Level.SEVERE, "Nieprawidlowy indeks wiersza: {0}", y);
            throw new ValidationException("validation.error.invalid_row_index");
        }
        return new GameOfLifeRow(new ArrayList<>(board.get(y)));
    }

    public GameOfLifeColumn getColumn(int x) {
        if (x < 0 || x >= columns) {
            LOGGER.log(Level.SEVERE, "Nieprawidlowy indeks kolumny: {0}", x);
            throw new ValidationException("validation.error.invalid_column_index");
        }
        List<GameOfLifeCell> column = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            column.add(board.get(i).get(x));
        }
        return new GameOfLifeColumn(column);
    }

    public void clear() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                getCell(i, j).setValue(false);
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("rows", rows)
                .append("columns", columns)
                .append("board", board)
                .append("simulator", simulator)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        GameOfLifeBoard that = (GameOfLifeBoard) obj;

        return new EqualsBuilder()
                .append(rows, that.rows)
                .append(columns, that.columns)
                .append(board, that.board)
                .append(simulator, that.simulator)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rows)
                .append(columns)
                .append(board)
                .append(simulator)
                .toHashCode();
    }

    @Override
    public GameOfLifeBoard clone() {
        try {
            GameOfLifeBoard clonedBoard = (GameOfLifeBoard) super.clone();

            List<List<GameOfLifeCell>> boardCopy = new ArrayList<>(this.rows);
            for (int i = 0; i < this.rows; i++) {
                List<GameOfLifeCell> rowCopy = new ArrayList<>(this.columns);
                for (int j = 0; j < this.columns; j++) {
                    rowCopy.add(this.board.get(i).get(j).clone());
                }
                boardCopy.add(rowCopy);
            }
            clonedBoard.board = boardCopy;
            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            LOGGER.log(Level.SEVERE, "Klonowanie sie nie powiodlo...", e);
            throw new BaseApplicationException("Klonowanie sie nie powiodlo...", e);
        }
    }
}
