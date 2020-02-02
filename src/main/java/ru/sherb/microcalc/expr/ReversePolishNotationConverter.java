package ru.sherb.microcalc.expr;

import java.util.ArrayDeque;
import java.util.Objects;

/**
 * @author maksim
 * @since 22.06.19
 */
public final class ReversePolishNotationConverter {

    public static class ExpressionConvertException extends RuntimeException {
    }

    public String toRPN(String expression) throws ExpressionConvertException {
        Objects.requireNonNull(expression);

        StringBuilder result = new StringBuilder();
        ArrayDeque<Character> operators = new ArrayDeque<>(); //todo CharStack, less boxing/unboxing

        for (int i = 0; i < expression.length(); i++) {
            var token = expression.charAt(i);
            if (Character.isSpaceChar(token)) {
                continue;
            }

            switch (token) {
                case '-':
                case '+':
                    while (!operators.isEmpty() && (operators.peek().equals('+') || operators.peek().equals('-'))) {
                        result.append(operators.pop()).append(" ");
                    }
                case '*':
                case '/':
                    while (!operators.isEmpty() && (operators.peek().equals('*') || operators.peek().equals('/'))) {
                        result.append(operators.pop()).append(" ");
                    }
                    operators.push(token);
                    break;
                case '(':
                    operators.push(token);
                    break;
                case ')':
                    while (!operators.isEmpty() && !operators.peek().equals('(')) {
                        result.append(operators.pop()).append(" ");
                    }
                    operators.pop();
                    break;
                default:
                    result.append(token).append(" ");
            }
        }

        while (!operators.isEmpty()) {
            result.append(operators.pop()).append(" ");
        }

        return result.toString().trim();
    }

}
