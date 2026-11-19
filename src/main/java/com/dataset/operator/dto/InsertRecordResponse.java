package com.dataset.operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after successfully persisting a JSON record into a dataset")
public record InsertRecordResponse(
        @Schema(example = "Record added successfully") String message,
        @Schema(example = "employee_dataset") String dataset,
        @Schema(example = "1") Object recordId
) {}
