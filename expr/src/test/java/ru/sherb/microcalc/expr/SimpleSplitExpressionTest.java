package ru.sherb.microcalc.expr;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksim
 * @since 02.02.2020
 */
class SimpleSplitExpressionTest {

    @Test
    public void testBuildGraph() {
        var expr = "1 2 + 5 3 * / 10 +";

        var graph = SimpleSplitExpression.buildGraph(expr);

        assertPartsEquals(
                pieces("1 2 +   => 0",
                       "5 3 *   => 1",
                       "$0 $1 / => 2",
                       "$2 10 + => 3"),
                graph.getKey());

        assertArrayEquals(new Integer[]{0, 1}, graph.getValue());

        //========================================================

        expr = "1 2 + 3 + 4 + 5 + 6 7 / +";

        graph = SimpleSplitExpression.buildGraph(expr);

        assertPartsEquals(
                pieces("1 2 +   => 0",
                        "$0 3 +  => 1",
                        "$1 4 +  => 2",
                        "$2 5 +  => 3",
                        "6 7 /   => 4",
                        "$3 $4 + => 5"),
                graph.getKey());

        assertArrayEquals(new Integer[]{0, 4}, graph.getValue());

        //========================================================

        var incorrectExpr = "1 + 2";

        assertThrows(RuntimeException.class, () -> {
            SimpleSplitExpression.buildGraph(incorrectExpr);
        });
    }

    @Test
    public void testCorrectRoots() {
        var expr = "1 2 + 3 + 5 3 * / 10 +";

        var splitExpr = SimpleSplitExpression.ofRpnExpr(expr);

        assertPartsEquals(
                pieces("1 2 + => 0",
                       "5 3 * => 2"),
                splitExpr.roots());
    }

    @Test
    public void testResolveAndNextSeq() {
        var expr = "1 2 + 3 4 * +";

        var splitExpr = SimpleSplitExpression.ofRpnExpr(expr);

        var roots = splitExpr.roots();
        assertEquals(2, roots.length);

        var next = splitExpr.resolveAndNext(roots[0], 1);
        assertTrue(next.isEmpty());
        assertFalse(splitExpr.isResolved());

        next = splitExpr.resolveAndNext(roots[1], 42);
        assertTrue(next.isPresent());
        assertPartsEquals(pieces("1 42 + => 2"), next.get());
        assertFalse(splitExpr.isResolved());

        next = splitExpr.resolveAndNext(next.get(), 2);
        assertTrue(next.isEmpty());
        assertTrue(splitExpr.isResolved());
        assertEquals(2, splitExpr.result());
    }

    @Test
    public void testResolveAndNextReverseSeq() {
        var expr = "1 2 + 3 + 5 3 * / 10 +";

        var splitExpr = SimpleSplitExpression.ofRpnExpr(expr);

        var roots = splitExpr.roots();
        assertEquals(2, roots.length);

        var next = splitExpr.resolveAndNext(roots[1], 42);
        assertTrue(next.isEmpty());
        assertFalse(splitExpr.isResolved());

        next = splitExpr.resolveAndNext(roots[0], 1);
        assertTrue(next.isPresent());
        assertPartsEquals(pieces("1 3 + => 1"), next.get());
        assertFalse(splitExpr.isResolved());

        next = splitExpr.resolveAndNext(next.get(), 2);
        assertTrue(next.isPresent());
        assertPartsEquals(pieces("2 42 / => 3"), next.get());
        assertFalse(splitExpr.isResolved());

        next = splitExpr.resolveAndNext(next.get(), 3);
        assertTrue(next.isPresent());
        assertPartsEquals(pieces("3 10 + => 4"), next.get());
        assertFalse(splitExpr.isResolved());

        next = splitExpr.resolveAndNext(next.get(), 4);
        assertTrue(next.isEmpty());
        assertTrue(splitExpr.isResolved());
        assertEquals(4, splitExpr.result());
    }

    private static ExprPart[] pieces(String... pieces) {
        var result = new ExprPart[pieces.length];

        for (int i = 0; i < pieces.length; i++) {
            String piece = pieces[i];
            var tmp = piece.split("=>");

            var expr = tmp[0].trim().split(" ");
            result[i] = new ExprPart(Integer.parseInt(tmp[1].trim()), expr[2], expr[0], expr[1]);
        }

        return result;
    }

    private void assertPartsEquals(ExprPart[] expectedParts, ExprPart... actualParts) {
        assertEquals(expectedParts.length, actualParts.length, () -> "Numbers of parts are not equals\n" + Arrays.toString(actualParts));

        for (int i = 0; i < expectedParts.length; i++) {
            var expected = expectedParts[i];
            var actual = actualParts[i];

            assertEquals(expected.id(), actual.id(), "Incorrect id");
            assertEquals(expected.operator(), actual.operator(), "Incorrect operator");
            assertArrayEquals(expected.args(), actual.args(), "Incorrect args");
        }
    }
}