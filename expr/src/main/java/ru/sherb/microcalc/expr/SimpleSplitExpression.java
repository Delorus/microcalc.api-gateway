package ru.sherb.microcalc.expr;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static ru.sherb.microcalc.expr.ExprPart.ID_PREFIX;

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
final class SimpleSplitExpression implements SplitExpression {

    /**
     * @param rpnExpr expression in reverse polish notation
     */
    public static SimpleSplitExpression ofRpnExpr(String rpnExpr) {
        Map.Entry<ExprPart[], Integer[]> parts = buildGraph(rpnExpr);
        return new SimpleSplitExpression(parts.getKey(), parts.getValue());
    }

    //region private support functions
    static Map.Entry<ExprPart[], Integer[]> buildGraph(String rpnExpr) {
        String[] tokens = rpnExpr.split(" ");
        int idCounter = 0;

        var result = new ArrayList<ExprPart>();
        var roots = new ArrayList<Integer>();

        var buff = new ArrayList<String>();
        for (String token : tokens) {

            String t = token.trim();

            if (isOperator(t)) {
                //fixme support only 2-arity operations (2 args + 1 op)
                if (buff.size() < 2) {
                    throw new RuntimeException("invalid expression, maybe is not in reverse polish notation?");
                }
                var piece = buff.subList(buff.size() - 2, buff.size());
                var p = new ExprPart(idCounter, t, piece.toArray(String[]::new));

                idCounter++;
                piece.clear();
                piece.add(ID_PREFIX + p.id());

                result.add(p);
                if (p.isMaterialized()) {
                    roots.add(p.id());
                }
            } else {
                buff.add(t);
            }
        }

        return Map.entry(result.toArray(ExprPart[]::new), roots.toArray(Integer[]::new));
    }

    //fixme must be external and generic function,
    // because it known about supported operators
    private static boolean isOperator(String token) {
        return "+".equals(token) || "-".equals(token) ||
                "*".equals(token) || "/".equals(token);
    }

    //endregion

    private final ExprPart[] parts;
    private final Integer[] roots;

    private SimpleSplitExpression(ExprPart[] parts, Integer[] roots) {
        assert parts != null;
        assert roots != null;
        assert (parts.length > 0 && roots.length > 0) || (parts.length == 0 && roots.length == 0);

        this.roots = roots;
        this.parts = parts;
    }

    @Override
    public Optional<ExprPart> resolveAndNext(ExprPart part, Number answer) {
        Objects.requireNonNull(part);
        Objects.requireNonNull(answer);
        if (parts[part.id()] != part) {
            throw new IllegalArgumentException("this piece is not part of the expression");
        }

        parts[part.id()].answer(answer);

        for (int i = part.id() + 1; i < parts.length; i++) {
            var next = parts[i];
            next.resolve(part.id(), part.answer());
            if (next.isMaterialized() && !next.hasAnswer()) {
                return Optional.of(next);
            }
        }

        return Optional.empty();
    }

    @Override
    public ExprPart[] roots() {
        var result = new ExprPart[this.roots.length];
        for (int i = 0; i < this.roots.length; i++) {
            result[i] = parts[this.roots[i]];
        }
        return result;
    }

    @Override
    public Number result() {
        if (!isResolved()) {
            throw new RuntimeException("expression not yet resolved");
        }

        return parts[parts.length-1].answer();
    }

    @Override
    public boolean isResolved() {
        for (ExprPart part : parts) {
            if (!part.isMaterialized()) {
                return false;
            }
        }

        return true;
    }
}