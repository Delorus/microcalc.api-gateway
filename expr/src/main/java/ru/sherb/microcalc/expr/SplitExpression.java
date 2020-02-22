package ru.sherb.microcalc.expr;

import java.util.Optional;

/**
 * @author maksim
 * @since 22.02.2020
 */
public interface SplitExpression {

    ExprPart[] roots();

    Optional<ExprPart> resolveAndNext(ExprPart part, Number answer);

    boolean isResolved();

    Number result();
}
