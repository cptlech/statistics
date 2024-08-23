package com.ll.statistics.controller;

import com.ll.statistics.domain.Symbol;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;


public record AddDataPointsRequest(@NotNull @DefaultValue("S1") Symbol symbol, @Size(min=1, max = AddDataPointsController.MAX_BATCH_SIZE) List<Float> values) {

}
