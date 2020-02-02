package ru.sherb.microcalc.expr;

import lombok.Value;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Split expressions into standalone pieces, e.g.:
 * <pre>
 * 1.  (1 + 2) / (5 * 3) + 10
 * 2.  1 2 + 5 3 * / 10 +
 * 3.  (1 2 +) => $1
 * 4.  $1 5 3 * / 10 +
 * 5.  (5 3 *) => $2
 * 6.  $1 $2 / 10 +
 * 7.  ($1 $2 /) => $3
 * 8.  $3 10 +
 * 9.  ($3 10 +) => $4
 * </pre>
 * <b>EXPRESSIONS MUST BE IN THE REVERSE POLISH NOTATION</b>
 * @implNote this is not a thread safe implementation
 * @author maksim
 * @since 23.06.19
 */
public final class SimpleSplitExpression {

    private final String rpnExpr;

    public SimpleSplitExpression(String rpnExpr) {
        Objects.requireNonNull(rpnExpr);
        assert !rpnExpr.isBlank();

        this.rpnExpr = rpnExpr;
    }

    public Piece[] pieces() {
        String[] tokens = rpnExpr.split(" ");
        int idCounter = 0;

        var result = new ArrayList<Piece>();
        var buff = new ArrayList<String>();
        for (String token : tokens) {

            String t = token.trim();
            buff.add(t);

            if (isOperator(t)) {
                //fixme support only 2-arity operations (2 args + 1 op)
                var piece = buff.subList(buff.size() - 3, buff.size());
                var p = new Piece("$" + idCounter, String.join(" ", piece));

                idCounter++;
                piece.clear();
                piece.add(p.id);

                result.add(p);
            }
        }

        return result.toArray(new Piece[0]);
    }

    //fixme must be external and generic function,
    // because it known about supported operators
    private boolean isOperator(String token) {
        return "+".equals(token) || "-".equals(token) ||
                "*".equals(token) || "/".equals(token);
    }

    @Value
    public static class Piece {
        String id;
        String expr;

        @Override
        public String toString() {
            return '(' + id + ", " + expr + ')';
        }
    }
}