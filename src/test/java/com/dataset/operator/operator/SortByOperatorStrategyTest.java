package com.dataset.operator.operator;

import com.dataset.operator.dto.SortByResponse;
import com.dataset.operator.enums.SortOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SortByOperatorStrategyTest {

    private final SortByOperatorStrategy strategy = new SortByOperatorStrategy();

    @Test
    @DisplayName("Should sort records numerically by age ascending")
    void shouldSortNumericAscending() {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "name", "John Doe", "age", 30, "department", "Engineering"),
                Map.of("id", 2, "name", "Jane Smith", "age", 25, "department", "Engineering"),
                Map.of("id", 3, "name", "Alice Brown", "age", 28, "department", "Marketing")
        );

        QueryContext context = new QueryContext(null, "age", SortOrder.ASC);
        SortByResponse response = strategy.execute(records, context);

        assertThat(response.sortedRecords())
                .extracting(r -> r.get("id"))
                .containsExactly(2, 3, 1);
    }

    @Test
    @DisplayName("Should sort records numerically by age descending")
    void shouldSortNumericDescending() {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "name", "John Doe", "age", 30),
                Map.of("id", 2, "name", "Jane Smith", "age", 25),
                Map.of("id", 3, "name", "Alice Brown", "age", 28)
        );

        QueryContext context = new QueryContext(null, "age", SortOrder.DESC);
        SortByResponse response = strategy.execute(records, context);

        assertThat(response.sortedRecords())
                .extracting(r -> r.get("age"))
                .containsExactly(30, 28, 25);
    }

    @Test
    @DisplayName("Should sort string fields case-insensitively and place missing fields last")
    void shouldSortStringsAndPlaceNullsLast() {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "name", "charlie"),
                Map.of("id", 2),
                Map.of("id", 3, "name", "Alice")
        );

        QueryContext context = new QueryContext(null, "name", SortOrder.ASC);
        SortByResponse response = strategy.execute(records, context);

        assertThat(response.sortedRecords())
                .extracting(r -> r.get("id"))
                .containsExactly(3, 1, 2);
    }
}
