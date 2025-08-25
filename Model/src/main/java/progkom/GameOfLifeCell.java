package progkom;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOfLifeCell implements Serializable, Comparable<GameOfLifeCell>, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(GameOfLifeCell.class); // Logger

    private boolean value;
    private List<GameOfLifeCell> neighbors;

    public GameOfLifeCell(boolean initialState) {
        this.value = initialState;
        this.neighbors = Collections.unmodifiableList(new ArrayList<>());
        logger.debug("GameOfLifeCell stworzony z początkowym stanem: {}", initialState);
    }

    public void setValue(boolean value) {
        this.value = value;
        logger.debug("Stan komórki ustawiony na: {}", value);
    }

    public boolean getCellValue() {
        logger.trace("Pobrano stan komórki: {}", value);
        return value;
    }

    public void setNeighbors(List<GameOfLifeCell> neighbors) {
        if (neighbors == null) {
            logger.error("Nieprawidlowa wartosc: null lista sasiadow.");
            throw new ValidationException("validation.error.invalid_neighbors_null");
        }

        if (neighbors.size() != 8) {
            logger.error("Nieprawidlowa liczba sasiadow: {}", neighbors.size());
            throw new ValidationException("validation.error.invalid_neighbors_count",
                    new IllegalArgumentException(String.valueOf(neighbors.size())));
        }

        this.neighbors = Collections.unmodifiableList(new ArrayList<>(neighbors));
        logger.info("Sąsiedzi komórki zaktualizowani. Liczba sąsiadów: {}", neighbors.size());
    }

    public List<GameOfLifeCell> getNeighbors() {
        logger.trace("Pobrano sąsiadów komórki.");
        return neighbors;
    }

    public boolean nextState() {
        int liveNeighbors = 0;
        for (GameOfLifeCell neighbor : neighbors) {
            if (neighbor != null && neighbor.getCellValue()) {
                liveNeighbors++;
            }
        }
        boolean nextState = value ? (liveNeighbors == 2 || liveNeighbors == 3) : (liveNeighbors == 3);
        logger.debug("Obliczono następny stan. Aktualny stan: {}, liczba żywych sąsiadów: {}, następny stan: {}",
                value, liveNeighbors, nextState);
        return nextState;
    }

    public void updateState(boolean newState) {
        logger.debug("Aktualizacja stanu komórki z {} na {}", value, newState);
        this.value = newState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("value", value)
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

        GameOfLifeCell that = (GameOfLifeCell) obj;

        boolean result = new EqualsBuilder()
                .append(value, that.value)
                .isEquals();

        logger.trace("Porównano komórki. Wynik: {}", result);
        return result;
    }

    @Override
    public int hashCode() {
        int hashCode = new HashCodeBuilder(17, 37)
                .append(value)
                .toHashCode();

        logger.trace("Obliczono hashCode: {}", hashCode);
        return hashCode;
    }

    @Override
    public int compareTo(GameOfLifeCell other) {
        if (other == null) {
            logger.error("Argument w porównaniu jest null");
            throw new ValidationException("validation.error.compare");
        }
        int result = Boolean.compare(value, other.value);
        logger.trace("Porównano komórki. Wynik porównania: {}", result);
        return result;
    }

    @Override
    public GameOfLifeCell clone() {
        try {
            GameOfLifeCell cloned = (GameOfLifeCell) super.clone();
            logger.debug("Skopiowano komórkę: {}", cloned);
            return cloned;
        } catch (CloneNotSupportedException e) {
            logger.error("Klonowanie się nie powiodło", e);
            throw new ValidationException("Klonowanie się nie powiodło...", e);
        }
    }
}
