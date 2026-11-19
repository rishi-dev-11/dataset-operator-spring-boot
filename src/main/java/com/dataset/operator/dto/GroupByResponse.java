package com.dataset.operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Response returned when querying a dataset with the groupBy operator")
public record GroupByResponse(
        @Schema(description = "Map of group key values to lists of matching JSON records")
        Map<String, List<Map<String, Object>>> groupedRecords
) {}
