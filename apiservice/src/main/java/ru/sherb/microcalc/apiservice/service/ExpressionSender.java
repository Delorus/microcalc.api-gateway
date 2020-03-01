package ru.sherb.microcalc.apiservice.service;

import ru.sherb.microcalc.expr.ExprPart;

import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 29.02.2020
 */
public interface ExpressionSender {

    List<Map.Entry<ExprPart, Number>> sendPart(ExprPart[] parts);
}
