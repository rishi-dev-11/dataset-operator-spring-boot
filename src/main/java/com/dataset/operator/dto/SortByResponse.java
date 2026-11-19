package com.dataset.operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Response returned when querying a dataset with the sortBy operator")
public record SortByResponse(
        @Schema(description = "Ordered list of JSON records sorted by the requested field and direction")
        List<Map<String, Object>> sortedRecords
) {}
