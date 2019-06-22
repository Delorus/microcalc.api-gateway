package ru.sherb.microcalc.apiservice.calc.v1;

/**
 * @author maksim
 * @since 22.06.19
 */
public final class CalcRequest {

    private String expr;

    public CalcRequest() {
    }

    public CalcRequest(String expr) {
        this.expr = expr;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }
}
