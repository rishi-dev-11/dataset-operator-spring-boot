package com.dataset.operator.operator;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for dynamic dataset query operators (Group-By, Sort-By, Raw).
 */
public interface QueryOperatorStrategy {

    boolean supports(QueryContext context);

    Object execute(List<Map<String, Object>> records, QueryContext context);
}
