package progkom;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GameOfLifeCellBase implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(GameOfLifeCellBase.class); // Logger

    private List<GameOfLifeCell> cells;

    public GameOfLifeCellBase(List<GameOfLifeCell> cells) {
        if (cells == null) {
            logger.error("Próba inicjalizacji z null listą.");
            throw new ValidationException("validation.error.null.value1", null);
        }
        for (GameOfLifeCell cell : cells) {
            if (cell == null) {
                logger.error("Lista komórek zawiera pusty element.");
                throw new ValidationException("validation.error.null.value", null);
            }
        }
        this.cells = new ArrayList<>(Collections.unmodifiableList(cells));
        logger.info("GameOfLifeCellBase zainicjalizowany. Liczba komórek: {}", cells.size());
    }

    private long checkStateOfCell(boolean check) {
        long count = cells.stream().filter(cell -> cell.getCellValue() == check).count();
        logger.debug("Sprawdzono stan komórek: {}. Liczba dopasowań: {}", check ? "żywe" : "martwe", count);
        return count;
    }

    public int countALiveCells() {
        int alive = (int) checkStateOfCell(true);
        logger.debug("Liczba żywych komórek: {}", alive);
        return alive;
    }

    public int countDeadCells() {
        int dead = (int) checkStateOfCell(false);
        logger.debug("Liczba martwych komórek: {}", dead);
        return dead;
    }

    public List<GameOfLifeCell> getCells() {
        logger.trace("Pobrano listę komórek.");
        return Collections.unmodifiableList(cells);
    }

    protected void setCells(List<GameOfLifeCell> cells) {
        if (cells == null) {
            logger.error("Próba ustawienia null listy komórek.");
            throw new ValidationException("Lista komórek nie może być null", null);
        }
        this.cells = cells;
        logger.info("Lista komórek została zmieniona. Nowa liczba komórek: {}", cells.size());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("cells", cells)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            logger.trace("Porównano dwa te same obiekty GameOfLifeCellBase.");
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            logger.debug("Porównano różne klasy: {} i {}", this.getClass(), obj != null ? obj.getClass() : "null");
            return false;
        }

        GameOfLifeCellBase that = (GameOfLifeCellBase) obj;

        boolean result = new EqualsBuilder()
                .append(cells, that.cells)
                .isEquals();

        logger.debug("Porównano dwa obiekty GameOfLifeCellBase. Wynik: {}", result);
        return result;
    }

    @Override
    public int hashCode() {
        int hashCode = new HashCodeBuilder(17, 37)
                .append(cells)
                .toHashCode();

        logger.trace("Obliczono hashCode: {}", hashCode);
        return hashCode;
    }
}
