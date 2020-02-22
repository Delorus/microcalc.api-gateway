package ru.sherb.microcalc.apiservice.calc.v1;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author maksim
 * @since 22.06.19
 */
@Data
@NoArgsConstructor
public final class CalcRequest {

    private String expr;
}
