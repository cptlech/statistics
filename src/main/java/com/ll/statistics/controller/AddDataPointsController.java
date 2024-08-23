package com.ll.statistics.controller;


import com.ll.statistics.domain.DataPoints;
import com.ll.statistics.domain.Calculator;
import com.ll.statistics.domain.Symbol;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddDataPointsController {

    public static final int MAX_BATCH_SIZE = 10000000;

    private final Calculator calculator;

    public AddDataPointsController(@Autowired Calculator calculator) {
        this.calculator = calculator;
    }

    @PostMapping(value="/add_batch")
    public void addDataPoints(@Valid @RequestBody AddDataPointsRequest addDataPointsRequest) {
        Symbol symbol = addDataPointsRequest.symbol();
        DataPoints dataPoints = calculator.getDataPointsMap().get(symbol);
        if (dataPoints.getLastIndex() + 1 + addDataPointsRequest.values().size() >
                DataPoints.DEFAULT_MAX_NUMBER_OF_DATA_POINTS) {
            throw new IllegalArgumentException("Maximum number of data points exceeded");
        }
        symbol.lock();
        dataPoints.addAll(addDataPointsRequest.values());
        calculator.updateStats(symbol);
        symbol.unlock();
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
