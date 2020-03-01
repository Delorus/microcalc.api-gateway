package ru.sherb.microcalc.apiservice.service;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sherb.microcalc.expr.ExprPart;
import ru.sherb.microcalc.expr.ExpressionSplitter;
import ru.sherb.microcalc.expr.SplitExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

/**
 * @author maksim
 * @since 29.02.2020
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DistributeCalcServiceTest {

    @MockBean
    private ExpressionSender expressionSender;

    @Autowired
    private DistributeCalcService distributeCalcService;

    @Test
    @DisplayName("When expression is empty then throw error")
    public void testCalculateThrowErrIfExprIsEmpty() {
        // Setup
        String blankExpr = "  ";
        String emptyExpr = "";
        String nullExpr = null;

        // Given
        assertThrows(IllegalArgumentException.class, () -> {
            distributeCalcService.calculate(blankExpr);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            distributeCalcService.calculate(emptyExpr);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            distributeCalcService.calculate(nullExpr);
        });
    }

    @Test
    @DisplayName("Correct calculation one-root expression")
    public void testCalculateLinearExpression() {
        // Setup
        String expr = "1 + 2 + 3 + 4 + 5 + 6";
        Number[] results = new Number[]{1, 2, 3, 4, 5};
        ExprPart[] parts = getExpectedParts(expr, results);
        setupResultOfPart(parts[0], results[0]);
        setupResultOfPart(parts[1], results[1]);
        setupResultOfPart(parts[2], results[2]);
        setupResultOfPart(parts[3], results[3]);
        setupResultOfPart(parts[4], results[4]);

        // When
        Number result = distributeCalcService.calculate(expr);

        // Then
        assertEquals(results[4], result);
    }

    @Test
    @DisplayName("Correct calculation multiple-roots expression")
    public void testCalculateSeveralRootsExpression() {
        // Setup
        String expr = "1 + 2 + 3 * 4";
        Number[] results = new Number[]{1, 3, 2};
        ExprPart[] parts = getExpectedParts(expr, results);
        setupResultOfPart(new ExprPart[]{parts[0], parts[1]},
                          new Number[]{results[0], results[1]});
        setupResultOfPart(parts[2], results[2]);

        // When
        Number result = distributeCalcService.calculate(expr);

        // Then
        assertEquals(results[2], result);
    }

    private ExprPart[] getExpectedParts(String raw, Number... answers) {
        SplitExpression expr = ExpressionSplitter.split(raw);
        ExprPart[] roots = expr.roots();

        List<ExprPart> result = new ArrayList<>(Arrays.asList(roots));
        for (int i = 0; i < answers.length; i++) {
            expr.resolveAndNext(result.get(i), answers[i])
                    .ifPresent(result::add);
        }
        return result.toArray(ExprPart[]::new);
    }

    private void setupResultOfPart(ExprPart part, Number result) {
        setupResultOfPart(new ExprPart[]{part}, new Number[]{result});
    }

    private void setupResultOfPart(ExprPart[] parts, Number[] results) {
        when(expressionSender.sendPart(argThat(eqParts(parts))))
                .thenAnswer(invocation -> {
                    ExprPart[] args = invocation.getArgument(0, ExprPart[].class);
                    List<Map.Entry<ExprPart, Number>> res = new ArrayList<>();
                    for (int i = 0, argsLength = args.length; i < argsLength; i++) {
                        res.add(Map.entry(args[i], results[i]));
                    }
                    return res;
                });
    }

    private ArgumentMatcher<ExprPart[]> eqParts(ExprPart... parts) {
        return args -> {
            if (parts == null || args == null) {
                return false;
            }

            boolean match = parts.length == args.length;
            for (int i = 0; i < parts.length; i++) {
                match &= parts[i].operator().equals(args[i].operator());
                match &= Arrays.equals(parts[i].args(), args[i].args());
            }
            return match;
        };
    }
}