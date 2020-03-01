package ru.sherb.microcalc.expr;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksim
 * @since 01.03.2020
 */
class ExpressionSplitterTest {

    @Test
    public void testSplitExpression() {
        String expr = "1 + 2 + 3";

        SplitExpression expression = ExpressionSplitter.split(expr);

        ExprPart[] roots = expression.roots();
        assertEquals(1, roots.length);
        assertArrayEquals(new String[]{"1", "2"}, roots[0].args());
        assertEquals("+", roots[0].operator());

        Optional<ExprPart> maybeNext = expression.resolveAndNext(roots[0], 3);
        assertTrue(maybeNext.isPresent());
        assertArrayEquals(new String[]{"3", "3"}, maybeNext.get().args());
        assertEquals("+", maybeNext.get().operator());
    }
}