package ru.sherb.microcalc.apiservice.endpoint.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sherb.microcalc.apiservice.service.DistributeCalcService;

/**
 * @author maksim
 * @since 22.06.19
 */
@RestController
@RequestMapping(value = {"/calc/v1", "/calc"},
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public final class CalcRestEndpoint {

    private final DistributeCalcService calcService;

    @Autowired
    public CalcRestEndpoint(DistributeCalcService calcService) {
        this.calcService = calcService;
    }

    @PostMapping
    public CalcResponse calculate(@RequestBody CalcRequest req) {
        calcService.calculate(req.getExpr());
        return new CalcResponse(42);
    }
}
