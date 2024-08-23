package com.ll.statistics.controller;

import com.ll.statistics.domain.Calculator;
import com.ll.statistics.domain.Statistics;
import com.ll.statistics.domain.Symbol;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class GetStatisticsController {

    private static final int[] POWERS_OF_TEN = {
            10,
            100,
            1000,
            10_000,
            100_000,
            1_000_000,
            10_000_000,
            100_000_000
    };

    private final Calculator calculator;

    public GetStatisticsController(@Autowired  Calculator calculator) {
        this.calculator = calculator;
    }
    @GetMapping("stats")
    public Statistics getStatistics(@RequestParam(defaultValue = "S1") String symbol, @Min(1) @Max(8) @RequestParam(defaultValue = "1") int k) {
        return calculator.getStats(Symbol.valueOf(symbol), POWERS_OF_TEN[k-1]);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void illegalArgumentException() {
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void constraintViolationException() {
    }
}
