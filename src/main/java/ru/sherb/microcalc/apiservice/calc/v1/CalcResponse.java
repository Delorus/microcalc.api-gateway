package ru.sherb.microcalc.apiservice.calc.v1;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author maksim
 * @since 22.06.19
 */
@JsonInclude(Include.NON_NULL)
public final class CalcResponse {

    private Integer answer;

    public CalcResponse() {
    }

    public CalcResponse(Integer answer) {
        this.answer = answer;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }
}
