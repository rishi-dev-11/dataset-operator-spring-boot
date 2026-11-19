package com.dataset.operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Response returned when querying a dataset without groupBy or sortBy operators")
public record RecordsResponse(
        @Schema(description = "All JSON records stored in the dataset in insertion order")
        List<Map<String, Object>> records
) {}
