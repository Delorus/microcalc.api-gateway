package ru.sherb.microcalc.apiservice.calc.v1;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.sherb.microcalc.expr.ReversePolishNotationConverter;

/**
 * @author maksim
 * @since 22.06.19
 */
@RestController
@RequestMapping(value = {"/calc/v1", "/calc"},
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public final class CalcRestEndpoint {

    private final ReversePolishNotationConverter rpnConverter;

    public CalcRestEndpoint() {
        this.rpnConverter = new ReversePolishNotationConverter();
    }

    @PostMapping
    public Mono<CalcResponse> calculate(@RequestBody CalcRequest req) {
        return Mono.just(new CalcResponse());
    }
}
