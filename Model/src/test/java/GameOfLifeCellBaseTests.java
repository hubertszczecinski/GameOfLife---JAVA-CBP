import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import progkom.*;

class GameOfLifeCellBaseTest {

    private GameOfLifeCellBase gameOfLifeCellBase;

    @BeforeEach
    void setUp() {
        gameOfLifeCellBase = new GameOfLifeCellBase(Arrays.asList(
                new GameOfLifeCell(false),
                new GameOfLifeCell(true),
                new GameOfLifeCell(false)
        )) {};
    }

    @Test
    void testConstructor() {
        List<GameOfLifeCell> cells = gameOfLifeCellBase.getCells();
        assertEquals(3, cells.size());
        assertFalse(cells.get(0).getCellValue());
        assertTrue(cells.get(1).getCellValue());
    }

    @Test
    void testConstructor_NullCells() {
        Exception exception = assertThrows(ValidationException.class, () ->
                new GameOfLifeCellBase(null) {});
        assertEquals("Lista komorek nie moze byc null", exception.getMessage());
    }

    @Test
    void testConstructor_NullElementInCells() {
        Exception exception = assertThrows(ValidationException.class, () ->
                new GameOfLifeCellBase(Arrays.asList(new GameOfLifeCell(true), null)) {});
        assertEquals("Lista nie moze zawierac pustych elementow", exception.getMessage());
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(gameOfLifeCellBase, gameOfLifeCellBase);
    }

    @Test
    void testEquals_Null() {
        assertNotEquals(null, gameOfLifeCellBase);
    }

    @Test
    void testEquals_DifferentClass() {
        String other = "NotGameOfLifeCellBase";
        assertNotEquals(gameOfLifeCellBase, other);
    }

    @Test
    void testEquals_DifferentCells() {
        GameOfLifeCellBase other = new GameOfLifeCellBase(Arrays.asList(
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(true)
        )) {};
        assertNotEquals(gameOfLifeCellBase, other);
    }

    @Test
    void testHashCode_SameObject() {
        assertEquals(gameOfLifeCellBase.hashCode(), gameOfLifeCellBase.hashCode());
    }

    @Test
    void testHashCode_DifferentObject() {
        GameOfLifeCellBase other = new GameOfLifeCellBase(Arrays.asList(
                new GameOfLifeCell(true),
                new GameOfLifeCell(false),
                new GameOfLifeCell(true)
        )) {};
        assertNotEquals(gameOfLifeCellBase.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_EquivalentObject() {
        GameOfLifeCellBase other = new GameOfLifeCellBase(Arrays.asList(
                new GameOfLifeCell(false),
                new GameOfLifeCell(true),
                new GameOfLifeCell(false)
        )) {};
        assertEquals(gameOfLifeCellBase.hashCode(), other.hashCode());
    }

    @Test
    void testToString() {
        String expected = new ToStringBuilder(gameOfLifeCellBase)
                .append("cells", gameOfLifeCellBase.getCells())
                .toString();
        assertEquals(expected, gameOfLifeCellBase.toString());
    }

    @Test
    void testToString_EmptyCells() {
        GameOfLifeCellBase emptyBase = new GameOfLifeCellBase(Collections.emptyList()) {};
        String expected = new ToStringBuilder(emptyBase)
                .append("cells", Collections.emptyList())
                .toString();
        assertEquals(expected, emptyBase.toString());
    }

    @Test
    void testImmutableCells() {
        List<GameOfLifeCell> cells = gameOfLifeCellBase.getCells();
        Exception exception = assertThrows(UnsupportedOperationException.class, () ->
                cells.add(new GameOfLifeCell(true))
        );
        assertNotNull(exception);
    }


    @Test
    void testEqualsWithNull() {
        GameOfLifeCellBase row = new GameOfLifeRow(List.of(new GameOfLifeCell(true)));
        assertNotEquals(row, null, "Equals powinno zwrócić false dla null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        GameOfLifeCellBase row = new GameOfLifeRow(List.of(new GameOfLifeCell(true)));
        String other = "Not a GameOfLifeCellBase";
        assertNotEquals(row, other, "Equals powinno zwrócić false dla obiektu innej klasy");
    }

    @Test
    void testEqualsWithSameClassAndEqualContent() {
        GameOfLifeCellBase row1 = new GameOfLifeRow(List.of(new GameOfLifeCell(true)));
        GameOfLifeCellBase row2 = new GameOfLifeRow(List.of(new GameOfLifeCell(true)));
        assertEquals(row1, row2, "Equals powinno zwrócić true dla obiektów tej samej klasy z tym samym stanem");
    }

    @Test
    void testEqualsWithSameClassAndDifferentContent() {
        GameOfLifeCellBase row1 = new GameOfLifeRow(List.of(new GameOfLifeCell(true)));
        GameOfLifeCellBase row2 = new GameOfLifeRow(List.of(new GameOfLifeCell(false)));
        assertNotEquals(row1, row2, "Equals powinno zwrócić false dla obiektów tej samej klasy z różnym stanem");
    }

}
