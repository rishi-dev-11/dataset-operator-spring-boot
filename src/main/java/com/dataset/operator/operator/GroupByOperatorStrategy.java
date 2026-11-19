package com.dataset.operator.operator;

import com.dataset.operator.dto.GroupByResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class GroupByOperatorStrategy implements QueryOperatorStrategy {

    public static final String UNASSIGNED_GROUP_KEY = "null";

    @Override
    public boolean supports(QueryContext context) {
        return context.hasGroupBy();
    }

    @Override
    public GroupByResponse execute(List<Map<String, Object>> records, QueryContext context) {
        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();

        for (Map<String, Object> record : records) {
            Object fieldValue = JsonPathExtractor.extract(record, context.groupBy());
            String groupKey = fieldValue == null ? UNASSIGNED_GROUP_KEY : String.valueOf(fieldValue);
            grouped.computeIfAbsent(groupKey, key -> new ArrayList<>()).add(record);
        }

        // If sortBy is also provided alongside groupBy, sort records within each group
        if (context.hasSortBy()) {
            JsonValueComparator comparator = new JsonValueComparator(context.sortBy(), context.sortOrder());
            grouped.values().forEach(groupList -> groupList.sort(comparator));
        }

        return new GroupByResponse(grouped);
    }
}
