package com.dataset.operator.enums;

import com.dataset.operator.exception.InvalidQueryParameterException;

import java.util.Locale;

public enum SortOrder {
    ASC,
    DESC;

    public static SortOrder fromString(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> throw new InvalidQueryParameterException(
                    "Invalid sort order '" + value + "'. Supported values are 'asc' or 'desc'."
            );
        };
    }
}
