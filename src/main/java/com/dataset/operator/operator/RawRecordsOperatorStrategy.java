package com.dataset.operator.operator;

import com.dataset.operator.dto.RecordsResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(3)
public class RawRecordsOperatorStrategy implements QueryOperatorStrategy {

    @Override
    public boolean supports(QueryContext context) {
        return !context.hasGroupBy() && !context.hasSortBy();
    }

    @Override
    public RecordsResponse execute(List<Map<String, Object>> records, QueryContext context) {
        return new RecordsResponse(List.copyOf(records));
    }
}
