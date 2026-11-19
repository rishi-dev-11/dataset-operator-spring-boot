package com.dataset.operator.operator;

import com.dataset.operator.dto.SortByResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class SortByOperatorStrategy implements QueryOperatorStrategy {

    @Override
    public boolean supports(QueryContext context) {
        return !context.hasGroupBy() && context.hasSortBy();
    }

    @Override
    public SortByResponse execute(List<Map<String, Object>> records, QueryContext context) {
        List<Map<String, Object>> sorted = new ArrayList<>(records);
        sorted.sort(new JsonValueComparator(context.sortBy(), context.sortOrder()));
        return new SortByResponse(sorted);
    }
}
