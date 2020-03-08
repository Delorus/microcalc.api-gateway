package ru.sherb.microcalc.apiservice.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * @author maksim
 * @since 29.02.2020
 */
class ResultResponse {

    private final String id;
    private final Number result;

    ResultResponse(@JsonProperty("id") @NonNull String id, @JsonProperty("result") @NonNull Number result) {
        this.id = id;
        this.result = result;
    }

    String getId() {
        return id;
    }

    Number getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultResponse that = (ResultResponse) o;
        return id.equals(that.id) &&
                result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, result);
    }

    @Override
    public String toString() {
        return "ResultResponse{" +
                "id='" + id + '\'' +
                ", result=" + result +
                '}';
    }
}
