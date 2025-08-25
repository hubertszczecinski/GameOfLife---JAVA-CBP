import org.junit.jupiter.api.Test;

import java.util.List;

import progkom.*;

import static org.junit.jupiter.api.Assertions.*;

class GameOfLifeColumnTest {

    @Test
    void testCloneCreatesDeepCopy() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(false);
        GameOfLifeColumn original = new GameOfLifeColumn(List.of(cell1, cell2));

        GameOfLifeColumn clone = original.clone();

        assertNotSame(original, clone);
        assertEquals(original, clone);
        for (int i = 0; i < original.getCells().size(); i++) {
            assertNotSame(original.getCells().get(i), clone.getCells().get(i));
        }
    }
}
