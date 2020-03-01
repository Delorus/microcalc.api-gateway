package ru.sherb.microcalc.apiservice.service.test;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sherb.microcalc.apiservice.service.DistributeCalcService;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author maksim
 * @since 29.02.2020
 */
@RunWith(SpringRunner.class)
public class DistributeCalcServiceTest {

    @Autowired
    private DistributeCalcService distributeCalcService;

    @Test
    @DisplayName("When expression is empty then throw error")
    public void testCalculateThrowErrIfExprIsEmpty() {
        // Setup
        String blankExpr = "  ";
        String emptyExpr = "";
        String nullExpr = null;

        // Given
        assertThrows(IllegalArgumentException.class, () -> {
            distributeCalcService.calculate(blankExpr);
        });
    }
}