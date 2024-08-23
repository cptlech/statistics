package com.ll.statistics.domain;

import com.ll.functionalinterface.QuintFunction;
import com.ll.statistics.controller.AddDataPointsController;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;


import java.util.*;

import static com.ll.statistics.domain.DataPoints.DEFAULT_MAX_NUMBER_OF_DATA_POINTS;
import static com.ll.statistics.domain.Symbol.S1;
import static com.ll.statistics.domain.Symbol.S2;
import static com.ll.statistics.domain.Symbol.S3;
import static com.ll.statistics.domain.Symbol.S4;
import static com.ll.statistics.domain.Symbol.S5;
import static com.ll.statistics.domain.Symbol.S6;
import static com.ll.statistics.domain.Symbol.S7;
import static com.ll.statistics.domain.Symbol.S8;
import static com.ll.statistics.domain.Symbol.S9;
import static com.ll.statistics.domain.Symbol.S10;


@Component
public class Calculator {

    private static final int DEFAULT_BUFFER_SKIP = 10;

    private final TriFunction<Float, Float, Integer, Float> MIN_FUNCTION = (x, y, index) -> Math.min(x, y);
    private final TriFunction<Float, Float, Integer, Float> MAX_FUNCTION = (x, y, index) -> Math.max(x, y);
    private final TriFunction<Float, Float, Integer, Float> AVERAGE_FUNCTION = (x, y, index) -> (x + (y - x) / (index + 1));
    private final QuintFunction<Float, Float, Float, Float, Integer, Float> VARIANCE_FUNCTION = (x, y, previousAverage, currentAverage, index) ->
            (x * (index == 0 ? 1 : index - 1) + ((y - previousAverage) * (y - currentAverage))) / (index == 0 ? 1 : index);

    public final int bufferSkip;
    public final int statBufferSize;

    private final Map<Symbol, List<Float>> minStatBuffers;
    private final Map<Symbol, List<Float>> maxStatBuffers;
    private final Map<Symbol, List<Float>> averageBuffers;
    private final Map<Symbol, List<Float>> varianceBuffers;
    private final Map<Symbol, DataPoints> dataPointsMap;
    private Map<Symbol, Integer> lastProcessedIndexMap;

    public Calculator(int bufferSkip, int maxNumberOfDataPoints) {
        this.dataPointsMap = Map.of(S1, new DataPoints(maxNumberOfDataPoints),
                S2, new DataPoints(maxNumberOfDataPoints),
                S3, new DataPoints(maxNumberOfDataPoints),
                S4, new DataPoints(maxNumberOfDataPoints),
                S5, new DataPoints(maxNumberOfDataPoints),
                S6, new DataPoints(maxNumberOfDataPoints),
                S7, new DataPoints(maxNumberOfDataPoints),
                S8, new DataPoints(maxNumberOfDataPoints),
                S9, new DataPoints(maxNumberOfDataPoints),
                S10, new DataPoints(maxNumberOfDataPoints));
        this.bufferSkip = bufferSkip;
        this.statBufferSize = maxNumberOfDataPoints / bufferSkip;
        this.minStatBuffers = new HashMap<>();
        this.maxStatBuffers = new HashMap<>();
        this.averageBuffers = new HashMap<>();
        this.varianceBuffers = new HashMap<>();
        this.lastProcessedIndexMap = new HashMap<>();
        Arrays.stream(Symbol.values()).forEach(
                symbol -> {
                    minStatBuffers.put(symbol, new ArrayList<>(DEFAULT_MAX_NUMBER_OF_DATA_POINTS / DEFAULT_BUFFER_SKIP));
                    maxStatBuffers.put(symbol, new ArrayList<>(DEFAULT_MAX_NUMBER_OF_DATA_POINTS / DEFAULT_BUFFER_SKIP));
                    averageBuffers.put(symbol, new ArrayList<>(DEFAULT_MAX_NUMBER_OF_DATA_POINTS / DEFAULT_BUFFER_SKIP));
                    varianceBuffers.put(symbol, new ArrayList<>(DEFAULT_MAX_NUMBER_OF_DATA_POINTS / DEFAULT_BUFFER_SKIP));
                    lastProcessedIndexMap.put(symbol, 0);
                }
        );

    }


    public Calculator() {
        this(DEFAULT_BUFFER_SKIP, DEFAULT_MAX_NUMBER_OF_DATA_POINTS);
    }

    private float getLastDataPoint(Symbol symbol) {
        DataPoints dataPoints = dataPointsMap.get(symbol);
        return dataPoints.get(lastProcessedIndexMap.get(symbol));
    }

    private Statistics calculate(Symbol symbol, int lastIndex, boolean updateBuffers) {
        DataPoints dataPoints = dataPointsMap.get(symbol);
        if (dataPoints.isEmpty()) {
            throw new IllegalArgumentException("No data points defined");
        }
        lastIndex = lastIndex - 1;
        int maxLastIndex = updateBuffers ? dataPoints.getLastIndex() : lastProcessedIndexMap.get(symbol);
        if (lastIndex > maxLastIndex) {
            throw new IllegalArgumentException("Not enough data points defined. Max last index: " + maxLastIndex);
        }
        int count = 0;
        List<Float> minStatBuffer = minStatBuffers.get(symbol);
        List<Float> maxStatBuffer = maxStatBuffers.get(symbol);
        List<Float> averageBuffer = averageBuffers.get(symbol);
        List<Float> varianceBuffer = varianceBuffers.get(symbol);

        float min;
        float max;
        float average;
        float variance;

        if (updateBuffers) {
            min = minStatBuffer.isEmpty() ? Float.MAX_VALUE : minStatBuffer.get(minStatBuffer.size() - 1);
            max = maxStatBuffer.isEmpty() ? Float.MIN_VALUE : maxStatBuffer.get(maxStatBuffer.size() - 1);
            average = averageBuffer.isEmpty() ? 0f : averageBuffer.get(averageBuffer.size() - 1);
            variance = varianceBuffer.isEmpty() ? 0f : varianceBuffer.get(varianceBuffer.size() - 1);
        } else {
            min = lastIndex >= bufferSkip ? minStatBuffer.get((lastIndex / bufferSkip) - 1) : Float.MAX_VALUE;
            max = lastIndex >= bufferSkip ? maxStatBuffer.get((lastIndex / bufferSkip) - 1) : Float.MIN_VALUE;
            average = lastIndex >= bufferSkip ? averageBuffer.get((lastIndex / bufferSkip) - 1) : 0f;
            variance = lastIndex >= bufferSkip ? varianceBuffer.get((lastIndex / bufferSkip) - 1) : 0f;
        }
        float previousAverage = average;

        while (count <= lastIndex) {
            min = MIN_FUNCTION.apply(min, dataPoints.get(maxLastIndex - count), count);
            max = MAX_FUNCTION.apply(max, dataPoints.get(maxLastIndex - count), count);
            average = AVERAGE_FUNCTION.apply(average, dataPoints.get(maxLastIndex - count), count);
            variance = VARIANCE_FUNCTION.apply(variance, dataPoints.get(maxLastIndex - count), previousAverage,
                    average, count);

            if (updateBuffers && ((count + 1) % bufferSkip == 0)) {
                minStatBuffer.add(min);
                maxStatBuffer.add(max);
                averageBuffer.add(average);
                varianceBuffer.add(variance);
            }
            previousAverage = average;
            count++;
        }

        lastProcessedIndexMap.put(symbol, dataPoints.getLastIndex());
        return new Statistics(min, max, average, variance, getLastDataPoint(symbol));
    }

    public Statistics getStats(Symbol symbol, int lastIndex) {
        return calculate(symbol, lastIndex, false);
    }

    public void updateStats(Symbol symbol) {
        calculate(symbol, dataPointsMap.get(symbol).getLastIndex(), true);
    }

    public Map<Symbol, DataPoints> getDataPointsMap() {
        return dataPointsMap;
    }
}
