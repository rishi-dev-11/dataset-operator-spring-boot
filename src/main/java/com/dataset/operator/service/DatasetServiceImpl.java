package com.dataset.operator.service;

import com.dataset.operator.dto.InsertRecordResponse;
import com.dataset.operator.entity.DatasetEntity;
import com.dataset.operator.entity.DatasetRecordEntity;
import com.dataset.operator.enums.SortOrder;
import com.dataset.operator.exception.DatasetNotFoundException;
import com.dataset.operator.exception.InvalidQueryParameterException;
import com.dataset.operator.exception.InvalidRecordPayloadException;
import com.dataset.operator.operator.QueryContext;
import com.dataset.operator.operator.QueryOperatorFactory;
import com.dataset.operator.operator.QueryOperatorStrategy;
import com.dataset.operator.repository.DatasetRecordRepository;
import com.dataset.operator.repository.DatasetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class DatasetServiceImpl implements DatasetService {

    private static final Pattern DATASET_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,128}$");
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<>() {};

    private final DatasetRepository datasetRepository;
    private final DatasetRecordRepository recordRepository;
    private final QueryOperatorFactory operatorFactory;
    private final ObjectMapper objectMapper;

    public DatasetServiceImpl(
            DatasetRepository datasetRepository,
            DatasetRecordRepository recordRepository,
            QueryOperatorFactory operatorFactory,
            ObjectMapper objectMapper
    ) {
        this.datasetRepository = datasetRepository;
        this.recordRepository = recordRepository;
        this.operatorFactory = operatorFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public InsertRecordResponse insertRecord(String datasetName, Map<String, Object> recordPayload) {
        String normalizedName = validateDatasetName(datasetName);
        if (recordPayload == null || recordPayload.isEmpty()) {
            throw new InvalidRecordPayloadException("Record payload must be a non-empty JSON object.");
        }

        DatasetEntity dataset = datasetRepository.findByName(normalizedName)
                .orElseGet(() -> datasetRepository.save(new DatasetEntity(normalizedName)));

        Object payloadId = recordPayload.get("id");
        String externalRecordId = payloadId != null ? String.valueOf(payloadId) : null;
        String serializedPayload = serializePayload(recordPayload);

        DatasetRecordEntity savedEntity = recordRepository.save(
                new DatasetRecordEntity(dataset, externalRecordId, serializedPayload)
        );

        Object returnedRecordId = payloadId != null ? payloadId : savedEntity.getId();
        return new InsertRecordResponse(
                "Record added successfully",
                dataset.getName(),
                returnedRecordId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Object queryDataset(String datasetName, String groupBy, String sortBy, String order) {
        String normalizedName = validateDatasetName(datasetName);
        SortOrder resolvedOrder = SortOrder.fromString(order);

        DatasetEntity dataset = datasetRepository.findByName(normalizedName)
                .orElseThrow(() -> new DatasetNotFoundException(normalizedName));

        List<Map<String, Object>> records = recordRepository.findByDatasetIdOrderByIdAsc(dataset.getId())
                .stream()
                .map(entity -> deserializePayload(entity.getPayloadJson()))
                .toList();

        QueryContext context = new QueryContext(
                normalizeOptionalParam(groupBy),
                normalizeOptionalParam(sortBy),
                resolvedOrder
        );

        QueryOperatorStrategy strategy = operatorFactory.resolve(context);
        return strategy.execute(records, context);
    }

    private String validateDatasetName(String datasetName) {
        if (datasetName == null || !DATASET_NAME_PATTERN.matcher(datasetName.trim()).matches()) {
            throw new InvalidQueryParameterException(
                    "Invalid datasetName. Only alphanumeric characters, underscores, and hyphens (1-128 chars) are allowed."
            );
        }
        return datasetName.trim();
    }

    private String normalizeOptionalParam(String param) {
        return (param == null || param.isBlank()) ? null : param.trim();
    }

    private String serializePayload(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new InvalidRecordPayloadException("Failed to serialize JSON record payload.", ex);
        }
    }

    private Map<String, Object> deserializePayload(String json) {
        try {
            return objectMapper.readValue(json, MAP_TYPE_REF);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize persisted JSON record.", ex);
        }
    }
}
