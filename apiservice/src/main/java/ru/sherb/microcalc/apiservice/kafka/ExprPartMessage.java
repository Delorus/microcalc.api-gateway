package ru.sherb.microcalc.apiservice.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * @author maksim
 * @since 29.02.2020
 */
class ExprPartMessage {

    private final String id;
    private final String values;

    ExprPartMessage(@NonNull String... values) {
        this(UUID.randomUUID().toString(), String.join(" ", Objects.requireNonNull(values)));
    }

    private ExprPartMessage(@JsonProperty("id") @NonNull String id, @JsonProperty("values") @NonNull String values) {
        this.id = id;
        this.values = values;
    }

    String getId() {
        return id;
    }

    String getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExprPartMessage that = (ExprPartMessage) o;
        return id.equals(that.id) &&
                values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, values);
    }

    @Override
    public String toString() {
        return "ExprPartMessage{" +
                "id='" + id + '\'' +
                ", values='" + values + '\'' +
                '}';
    }
}
