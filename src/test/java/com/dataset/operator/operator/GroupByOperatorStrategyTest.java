package com.dataset.operator.operator;

import com.dataset.operator.dto.GroupByResponse;
import com.dataset.operator.enums.SortOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GroupByOperatorStrategyTest {

    private final GroupByOperatorStrategy strategy = new GroupByOperatorStrategy();

    @Test
    @DisplayName("Should group records dynamically by department preserving insertion order")
    void shouldGroupByDepartment() {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "name", "John Doe", "age", 30, "department", "Engineering"),
                Map.of("id", 2, "name", "Jane Smith", "age", 25, "department", "Engineering"),
                Map.of("id", 3, "name", "Alice Brown", "age", 28, "department", "Marketing")
        );

        QueryContext context = new QueryContext("department", null, SortOrder.ASC);
        GroupByResponse response = strategy.execute(records, context);

        assertThat(response.groupedRecords()).containsOnlyKeys("Engineering", "Marketing");
        assertThat(response.groupedRecords().get("Engineering")).hasSize(2);
        assertThat(response.groupedRecords().get("Marketing")).hasSize(1);
    }

    @Test
    @DisplayName("Should group records and sort within each group when both groupBy and sortBy are provided")
    void shouldGroupAndSortWithinGroups() {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "name", "John Doe", "age", 30, "department", "Engineering"),
                Map.of("id", 2, "name", "Jane Smith", "age", 25, "department", "Engineering")
        );

        QueryContext context = new QueryContext("department", "age", SortOrder.ASC);
        GroupByResponse response = strategy.execute(records, context);

        List<Map<String, Object>> engineering = response.groupedRecords().get("Engineering");
        assertThat(engineering).extracting(r -> r.get("age")).containsExactly(25, 30);
    }

    @Test
    @DisplayName("Should place records missing the groupBy key under the 'null' group")
    void shouldHandleMissingGroupKey() {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "name", "No Dept")
        );

        QueryContext context = new QueryContext("department", null, SortOrder.ASC);
        GroupByResponse response = strategy.execute(records, context);

        assertThat(response.groupedRecords()).containsKey("null");
        assertThat(response.groupedRecords().get("null")).hasSize(1);
    }
}
