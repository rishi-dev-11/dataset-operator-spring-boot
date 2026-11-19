package com.dataset.operator.service;

import com.dataset.operator.dto.InsertRecordResponse;

import java.util.Map;

public interface DatasetService {

    InsertRecordResponse insertRecord(String datasetName, Map<String, Object> recordPayload);

    Object queryDataset(String datasetName, String groupBy, String sortBy, String order);
}
