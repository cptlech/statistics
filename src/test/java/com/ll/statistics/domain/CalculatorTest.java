package com.ll.statistics.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    public static final double FLOAT_COMPARISON_THRESHOLD = 0.0001;
    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        this.calculator = new Calculator(10, 100);
    }

    private static void assertEqualsWithThreshold(float expected, float value) {
        assertTrue(Math.abs(value - expected) < FLOAT_COMPARISON_THRESHOLD);
    }

    @Test
    void getStatisticsWhenNoDataPoints() {
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.getStats(Symbol.S1, 1),
                "Expected doThing() to throw IllegalArgumentException, but it didn't"
        );
        assertEquals("No data points defined", illegalArgumentException.getMessage());
    }

    @Test
    void getStatisticsWhenNoBufferSkip() {
        List<Float> dataPoints1 = List.of(0.1f, 1.3f, -0.5f, 104.5f);
        List<Float> dataPoints2 = List.of(3.2f, 1.1f);
        Map<Symbol, DataPoints> dataPointsMap = calculator.getDataPointsMap();
        DataPoints dataPoints = dataPointsMap.get(Symbol.S1);
        dataPoints.addAll(dataPoints1);
        dataPoints.addAll(dataPoints2);
        calculator.updateStats(Symbol.S1);

        Statistics statistics = calculator.getStats(Symbol.S1, 1);

        assertEquals(1.1f, statistics.last());
        assertEquals(1.1f, statistics.min());
        assertEquals(1.1f, statistics.max());
        assertEquals(1.1f, statistics.avg());
        assertEquals(0f, statistics.var());

        statistics = calculator.getStats(Symbol.S1, 6);

        assertEquals(1.1f, statistics.last());
        assertEquals(-0.5f, statistics.min());
        assertEquals(104.5f, statistics.max());
        assertEquals(18.283333f, statistics.avg());
        assertEquals(1785.5937f, statistics.var());
    }

    @Test
    void getStatisticsWithBufferSkip() {
        this.calculator = new Calculator(2, 100);
        List<Float> dataPoints1 = List.of(0.1f, 1.3f, -0.5f, 104.5f);
        List<Float> dataPoints2 = List.of(3.2f, 1.1f);
        Map<Symbol, DataPoints> dataPointsMap = calculator.getDataPointsMap();
        DataPoints dataPoints = dataPointsMap.get(Symbol.S1);
        dataPoints.addAll(dataPoints1);
        dataPoints.addAll(dataPoints2);
        calculator.updateStats(Symbol.S1);

        Statistics statistics = calculator.getStats(Symbol.S1, 1);

        assertEquals(1.1f, statistics.last());
        assertEquals(1.1f, statistics.min());
        assertEquals(1.1f, statistics.max());
        assertEquals(1.1f, statistics.avg());
        assertEquals(0f, statistics.var());

        statistics = calculator.getStats(Symbol.S1, 6);

        assertEquals(1.1f, statistics.last());
        assertEquals(-0.5f, statistics.min());
        assertEquals(104.5f, statistics.max());
        assertEquals(18.283333f, statistics.avg());
        assertEquals(1785.5937f, statistics.var());
    }

    @Test
    void getStatisticsForDifferentSymbols() {
        this.calculator = new Calculator(2, 100);
        List<Float> dataPoints1 = List.of(0.1f, 1.3f, -0.5f, 104.5f);
        List<Float> dataPoints2 = List.of(3.2f, 1.1f);
        Map<Symbol, DataPoints> dataPointsMap = calculator.getDataPointsMap();
        DataPoints dataPointsS1 = dataPointsMap.get(Symbol.S1);
        DataPoints dataPointsS2 = dataPointsMap.get(Symbol.S2);
        dataPointsS1.addAll(dataPoints1);
        dataPointsS2.addAll(dataPoints2);
        calculator.updateStats(Symbol.S1);
        calculator.updateStats(Symbol.S2);

        Statistics statistics = calculator.getStats(Symbol.S1, 1);

        assertEquals(104.5f, statistics.last());
        assertEquals(104.5f, statistics.min());
        assertEquals(104.5f, statistics.max());
        assertEquals(104.5f, statistics.avg());
        assertEquals(0f, statistics.var());

        statistics = calculator.getStats(Symbol.S2, 1);

        assertEquals(1.1f, statistics.last());
        assertEquals(1.1f, statistics.min());
        assertEquals(1.1f, statistics.max());
        assertEquals(1.1f, statistics.avg());
        assertEquals(0f, statistics.var());
    }

}