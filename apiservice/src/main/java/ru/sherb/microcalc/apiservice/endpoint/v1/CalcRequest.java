package ru.sherb.microcalc.apiservice.endpoint.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author maksim
 * @since 22.06.19
 */
public final class CalcRequest {

    private final String expr;

    CalcRequest(@JsonProperty("expr") String expr) {
        this.expr = expr;
    }

    String getExpr() {
        return expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalcRequest that = (CalcRequest) o;
        return expr.equals(that.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr);
    }

    @Override
    public String toString() {
        return "CalcRequest{" +
                "expr='" + expr + '\'' +
                '}';
    }
}
