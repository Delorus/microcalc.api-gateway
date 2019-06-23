package ru.sherb.microcalc.apiservice.calc.v1;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.sherb.microcalc.apiservice.calc.v1.ReversePolishNotationConverter.ExpressionConvertException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author maksim
 * @since 22.06.19
 */
public class ReversePolishNotationConverterTest {

    @Test
    public void plusSingleExprWithoutSpaces() {
        var converter = new ReversePolishNotationConverter();

        var actual = converter.toRPN("2+3");

        assertEquals("2 3 +", actual);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '=',
            value = {"2 + 3=2 3 +", "2 - 3=2 3 -", "2 * 3=2 3 *", "2 / 3=2 3 /"})
    public void singleExpression(String expr, String expected) {
        var converter = new ReversePolishNotationConverter();

        var actual = converter.toRPN(expr);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '=', value = {
            "2 * 3 + 4=2 3 * 4 +", "2 + 3 * 4=2 3 4 * +",
            "2 * 3 / 4=2 3 * 4 /", "2 / 3 * 4=2 3 / 4 *",
            "2 + 3 - 4=2 3 + 4 -", "2 - 3 + 4=2 3 - 4 +"})
    public void expressionWithVariousPriority(String expr, String expected) {
        var converter = new ReversePolishNotationConverter();

        var actual = converter.toRPN(expr);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '=', value = {
            "2 * (3 + 4)=2 3 4 + *", "(2 * 3) + 4=2 3 * 4 +",
            "2 - (3 - 4)=2 3 4 - -", "(2 - 3) - 4=2 3 - 4 -"})
    public void expressionWithBrackets(String expr, String expected) {
        var converter = new ReversePolishNotationConverter();

        var actual = converter.toRPN(expr);

        assertEquals(expected, actual);
    }

    @Disabled("not supported")
    @ParameterizedTest
    @CsvSource(delimiter = '=', value = {
            "-1 + 3=-1 3 +"})
    public void expressionWithNegativeNumbers(String expr, String expected) {
        var converter = new ReversePolishNotationConverter();

        var actual = converter.toRPN(expr);

        assertEquals(expected, actual);
    }

    @Disabled("not supported")
    @ParameterizedTest
    @CsvSource(delimiter = '=', value = {
            "1.0 + 3.123=1.0 3.123 +"})
    public void expressionWithFloatNumbers(String expr, String expected) {
        var converter = new ReversePolishNotationConverter();

        var actual = converter.toRPN(expr);

        assertEquals(expected, actual);
    }

    @Disabled("not supported")
    @ParameterizedTest
    @ValueSource(strings = {"- 1", "(2 + 3", "3 +", "2 + 3)", ")("})
    public void incorrectExpression(String expr) {
        var converter = new ReversePolishNotationConverter();

        StringBuilder result = new StringBuilder();
        assertThrows(ExpressionConvertException.class,
                () -> result.append(converter.toRPN(expr)),
                result::toString);
    }
}