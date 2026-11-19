package com.dataset.operator.operator;

import com.dataset.operator.enums.SortOrder;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

/**
 * Type-aware comparator for dynamic JSON values.
 * Handles numeric, boolean, and case-insensitive string values while keeping null/missing values last.
 */
public class JsonValueComparator implements Comparator<Map<String, Object>> {

    private final String sortByField;
    private final SortOrder sortOrder;

    public JsonValueComparator(String sortByField, SortOrder sortOrder) {
        this.sortByField = sortByField;
        this.sortOrder = sortOrder == null ? SortOrder.ASC : sortOrder;
    }

    @Override
    public int compare(Map<String, Object> left, Map<String, Object> right) {
        Object leftVal = JsonPathExtractor.extract(left, sortByField);
        Object rightVal = JsonPathExtractor.extract(right, sortByField);

        if (leftVal == null && rightVal == null) {
            return 0;
        }
        if (leftVal == null) {
            return 1; // Always push missing/null values to the end
        }
        if (rightVal == null) {
            return -1;
        }

        int comparison = compareNonNullValues(leftVal, rightVal);
        return sortOrder == SortOrder.DESC ? -comparison : comparison;
    }

    private int compareNonNullValues(Object left, Object right) {
        if (left instanceof Number lNum && right instanceof Number rNum) {
            return new BigDecimal(lNum.toString()).compareTo(new BigDecimal(rNum.toString()));
        }
        if (left instanceof Boolean lBool && right instanceof Boolean rBool) {
            return Boolean.compare(lBool, rBool);
        }
        return left.toString().compareToIgnoreCase(right.toString());
    }
}
