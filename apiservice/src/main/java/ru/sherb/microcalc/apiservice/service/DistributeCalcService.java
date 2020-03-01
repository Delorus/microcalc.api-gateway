package ru.sherb.microcalc.apiservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.sherb.microcalc.expr.ExprPart;
import ru.sherb.microcalc.expr.ExpressionSplitter;
import ru.sherb.microcalc.expr.SplitExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maksim
 * @since 23.02.2020
 */
@Service
public class DistributeCalcService {

    private final ExpressionSender expressionSender;

    @Autowired
    public DistributeCalcService(ExpressionSender expressionSender) {
        this.expressionSender = expressionSender;
    }

    public Number calculate(String expr) {
        Assert.hasText(expr, "expression must not be empty");

        SplitExpression expression = ExpressionSplitter.split(expr);

        ExprPart[] nexts = expression.roots();
        if (nexts.length == 0) {
            throw new IllegalArgumentException("invalid expression: " + expr);
        }

        while (!expression.isResolved()) {
            if (nexts.length == 0) {
                throw new IllegalArgumentException("invalid expression, cannot fully resolved: " + expr);
            }

            var result = expressionSender.sendPart(nexts);

            List<ExprPart> parts = new ArrayList<>();
            result.forEach(entry -> expression.resolveAndNext(entry.getKey(), entry.getValue())
                    .ifPresent(parts::add));

            nexts = parts.toArray(ExprPart[]::new);
        }

        return expression.result();
    }

}
