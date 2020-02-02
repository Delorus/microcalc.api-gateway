package ru.sherb.microcalc.expr;

import org.junit.jupiter.api.Test;
import ru.sherb.microcalc.expr.SimpleSplitExpression.Piece;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author maksim
 * @since 02.02.2020
 */
class SimpleSplitExpressionTest {

    @Test
    public void testSplitToPiecesSimpleExpression() {
        var expr = "1 2 + 5 3 * / 10 +";

        SimpleSplitExpression sse = new SimpleSplitExpression(expr);

        assertArrayEquals(
                pieces("1 2 +   => $0",
                       "5 3 *   => $1",
                       "$0 $1 / => $2",
                       "$2 10 + => $3"),
                sse.pieces());
    }


    private static Piece[] pieces(String... pieces) {
        var result = new Piece[pieces.length];

        for (int i = 0; i < pieces.length; i++) {
            String piece = pieces[i];
            var tmp = piece.split("=>");

            result[i] = new Piece(tmp[1].trim(), tmp[0].trim());
        }

        return result;
    }
}