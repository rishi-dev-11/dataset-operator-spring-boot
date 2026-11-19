package com.dataset.operator.service;

import com.dataset.operator.dto.InsertRecordResponse;
import com.dataset.operator.entity.DatasetEntity;
import com.dataset.operator.entity.DatasetRecordEntity;
import com.dataset.operator.exception.DatasetNotFoundException;
import com.dataset.operator.exception.InvalidQueryParameterException;
import com.dataset.operator.exception.InvalidRecordPayloadException;
import com.dataset.operator.operator.GroupByOperatorStrategy;
import com.dataset.operator.operator.QueryOperatorFactory;
import com.dataset.operator.operator.RawRecordsOperatorStrategy;
import com.dataset.operator.operator.SortByOperatorStrategy;
import com.dataset.operator.repository.DatasetRecordRepository;
import com.dataset.operator.repository.DatasetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatasetServiceImplTest {

    @Mock
    private DatasetRepository datasetRepository;

    @Mock
    private DatasetRecordRepository recordRepository;

    private DatasetServiceImpl service;

    @BeforeEach
    void setUp() {
        QueryOperatorFactory factory = new QueryOperatorFactory(List.of(
                new GroupByOperatorStrategy(),
                new SortByOperatorStrategy(),
                new RawRecordsOperatorStrategy()
        ));
        service = new DatasetServiceImpl(datasetRepository, recordRepository, factory, new ObjectMapper());
    }

    @Test
    @DisplayName("Should create dataset if missing and persist JSON record")
    void shouldInsertRecordIntoNewDataset() {
        DatasetEntity savedDataset = new DatasetEntity("employee_dataset");
        when(datasetRepository.findByName("employee_dataset")).thenReturn(Optional.empty());
        when(datasetRepository.save(any(DatasetEntity.class))).thenReturn(savedDataset);
        when(recordRepository.save(any(DatasetRecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> payload = Map.of("id", 1, "name", "John Doe", "age", 30, "department", "Engineering");
        InsertRecordResponse response = service.insertRecord("employee_dataset", payload);

        assertThat(response.message()).isEqualTo("Record added successfully");
        assertThat(response.dataset()).isEqualTo("employee_dataset");
        assertThat(response.recordId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should reject empty record payloads with InvalidRecordPayloadException")
    void shouldRejectEmptyPayload() {
        assertThatThrownBy(() -> service.insertRecord("employee_dataset", Map.of()))
                .isInstanceOf(InvalidRecordPayloadException.class);
    }

    @Test
    @DisplayName("Should throw DatasetNotFoundException when querying unknown dataset")
    void shouldThrowWhenDatasetNotFound() {
        when(datasetRepository.findByName("unknown_ds")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.queryDataset("unknown_ds", "department", null, "asc"))
                .isInstanceOf(DatasetNotFoundException.class);
    }

    @Test
    @DisplayName("Should reject unsupported sort order parameter")
    void shouldRejectInvalidSortOrder() {
        assertThatThrownBy(() -> service.queryDataset("employee_dataset", null, "age", "sideways"))
                .isInstanceOf(InvalidQueryParameterException.class)
                .hasMessageContaining("Supported values are 'asc' or 'desc'");
    }
}
