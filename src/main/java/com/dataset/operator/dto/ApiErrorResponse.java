package com.dataset.operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standardized REST API error payload")
public record ApiErrorResponse(
        @Schema(example = "2026-11-19T12:00:00Z") Instant timestamp,
        @Schema(example = "400") int status,
        @Schema(example = "Bad Request") String error,
        @Schema(example = "Invalid sort order 'random'. Supported values are 'asc' or 'desc'.") String message,
        @Schema(example = "/api/dataset/employee_dataset/query") String path
) {}
