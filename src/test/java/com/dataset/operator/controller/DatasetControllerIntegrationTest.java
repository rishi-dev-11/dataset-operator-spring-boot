package com.dataset.operator.controller;

import com.dataset.operator.repository.DatasetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DatasetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatasetRepository datasetRepository;

    @BeforeEach
    void cleanDatabase() {
        datasetRepository.deleteAll();
    }

    @Test
    @DisplayName("End-to-end flow: Insert records, query with groupBy, and query with sortBy")
    void shouldInsertGroupAndSortRecordsEndToEnd() throws Exception {
        insertEmployee("""
                {"id": 1, "name": "John Doe", "age": 30, "department": "Engineering"}
                """, 1);
        insertEmployee("""
                {"id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering"}
                """, 2);
        insertEmployee("""
                {"id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing"}
                """, 3);

        // 1. Group-By department
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                        .param("groupBy", "department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering.length()").value(2))
                .andExpect(jsonPath("$.groupedRecords.Marketing.length()").value(1))
                .andExpect(jsonPath("$.groupedRecords.Marketing[0].name").value("Alice Brown"));

        // 2. Sort-By age ascending
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                        .param("sortBy", "age")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].id").value(2))
                .andExpect(jsonPath("$.sortedRecords[1].id").value(3))
                .andExpect(jsonPath("$.sortedRecords[2].id").value(1));
    }

    @Test
    @DisplayName("Should return 404 structured error when querying non-existent dataset")
    void shouldReturn404ForMissingDataset() throws Exception {
        mockMvc.perform(get("/api/dataset/does_not_exist/query")
                        .param("groupBy", "department"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("Should return 400 structured error for invalid sort order")
    void shouldReturn400ForInvalidOrder() throws Exception {
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                        .param("sortBy", "age")
                        .param("order", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private void insertEmployee(String json, int expectedId) throws Exception {
        mockMvc.perform(post("/api/dataset/employee_dataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Record added successfully"))
                .andExpect(jsonPath("$.dataset").value("employee_dataset"))
                .andExpect(jsonPath("$.recordId").value(expectedId));
    }
}
