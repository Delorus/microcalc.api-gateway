package ru.sherb.microcalc.apiservice.calc.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author maksim
 * @since 22.06.19
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public final class CalcResponse {

    private Integer answer;
}
