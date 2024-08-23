package com.ll.statistics.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataPoints {

    public static final int DEFAULT_MAX_NUMBER_OF_DATA_POINTS = 100000000;

    private final List<Float> buffer;

    public DataPoints() {
        this(DEFAULT_MAX_NUMBER_OF_DATA_POINTS);
    }

    public DataPoints(int maxNumberOfDataPoints) {
        buffer = new ArrayList<>(maxNumberOfDataPoints);
    }

    public void addAll(List<Float> points) {
        buffer.addAll(points);
    }

    public float get(int index) {
        return buffer.get(index);
    }

    public int getLastIndex() {
        return buffer.size() - 1;
    }

    public float getLast() {
        return this.buffer.get(getLastIndex());
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }
}
