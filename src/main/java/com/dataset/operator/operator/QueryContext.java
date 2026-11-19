package com.dataset.operator.operator;

import com.dataset.operator.enums.SortOrder;

public record QueryContext(
        String groupBy,
        String sortBy,
        SortOrder sortOrder
) {
    public boolean hasGroupBy() {
        return groupBy != null && !groupBy.isBlank();
    }

    public boolean hasSortBy() {
        return sortBy != null && !sortBy.isBlank();
    }
}
