package ru.sherb.microcalc.expr;

/**
 * @author maksim
 * @since 22.02.2020
 */
public class ExpressionSplitter {

    public static SplitExpression split(String expression) throws ExpressionConvertException {
        var rpn = new ReversePolishNotationConverter(expression).toRPN();
        return SimpleSplitExpression.ofRpnExpr(rpn);
    }

}
