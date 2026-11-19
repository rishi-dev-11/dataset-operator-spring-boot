package com.dataset.operator.operator;

import com.dataset.operator.exception.InvalidQueryParameterException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory resolving the matching QueryOperatorStrategy for a given QueryContext.
 */
@Component
public class QueryOperatorFactory {

    private final List<QueryOperatorStrategy> strategies;

    public QueryOperatorFactory(List<QueryOperatorStrategy> strategies) {
        this.strategies = strategies;
    }

    public QueryOperatorStrategy resolve(QueryContext context) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(context))
                .findFirst()
                .orElseThrow(() -> new InvalidQueryParameterException(
                        "No query operator strategy available for the supplied query parameters."
                ));
    }
}
