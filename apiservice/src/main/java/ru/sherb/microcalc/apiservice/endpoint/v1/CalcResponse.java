package ru.sherb.microcalc.apiservice.endpoint.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;


/**
 * @author maksim
 * @since 22.06.19
 */
public final class CalcResponse {

    private final Integer answer;

    CalcResponse(@JsonProperty("answer") Integer answer) {
        this.answer = answer;
    }

    Integer getAnswer() {
        return answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalcResponse that = (CalcResponse) o;
        return answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }

    @Override
    public String toString() {
        return "CalcResponse{" +
                "answer=" + answer +
                '}';
    }
}
