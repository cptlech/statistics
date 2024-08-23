package com.ll.statistics.domain;

public record Statistics(float min, float max, float avg, float var, float last) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return Float.compare(min, that.min) == 0 && Float.compare(max, that.max) == 0 && Float.compare(avg, that.avg) == 0 && Float.compare(var, that.var) == 0 && Float.compare(last, that.last) == 0;
    }

}
