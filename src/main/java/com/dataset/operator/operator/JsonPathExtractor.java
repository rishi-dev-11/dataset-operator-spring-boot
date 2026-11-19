package com.dataset.operator.operator;

import java.util.Map;

/**
 * Utility for safely extracting top-level or dot-delimited nested values
 * from dynamic JSON records (e.g., "department" or "address.city").
 */
public final class JsonPathExtractor {

    private JsonPathExtractor() {
    }

    @SuppressWarnings("unchecked")
    public static Object extract(Map<String, Object> record, String fieldPath) {
        if (record == null || fieldPath == null || fieldPath.isBlank()) {
            return null;
        }
        String[] segments = fieldPath.trim().split("\\.");
        Object current = record;
        for (String segment : segments) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = ((Map<String, Object>) map).get(segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }
}
