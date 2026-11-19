package com.dataset.operator.controller;

import com.dataset.operator.dto.ApiErrorResponse;
import com.dataset.operator.dto.GroupByResponse;
import com.dataset.operator.dto.InsertRecordResponse;
import com.dataset.operator.dto.SortByResponse;
import com.dataset.operator.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/dataset", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dataset Operators", description = "Insert dynamic JSON records and execute Group-By / Sort-By query operators")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping(value = "/{datasetName}/record", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Insert a JSON record into a dataset",
            description = "Persists an arbitrary JSON object into the named dataset in the relational database. Creates the dataset automatically if it does not yet exist."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Record persisted successfully",
                    content = @Content(
                            schema = @Schema(implementation = InsertRecordResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Record added successfully",
                                              "dataset": "employee_dataset",
                                              "recordId": 1
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid dataset name or empty/malformed JSON payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<InsertRecordResponse> insertRecord(
            @Parameter(description = "Unique dataset identifier", example = "employee_dataset")
            @PathVariable String datasetName,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dynamic JSON record payload",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Employee Record",
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "John Doe",
                                              "age": 30,
                                              "department": "Engineering"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Map<String, Object> recordPayload
    ) {
        InsertRecordResponse response = datasetService.insertRecord(datasetName, recordPayload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    @Operation(
            summary = "Query a dataset with Group-By and/or Sort-By operators",
            description = "Fetches persisted JSON records from the relational database and applies dynamic groupBy and/or sortBy transformations."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Query executed successfully",
                    content = @Content(
                            schema = @Schema(oneOf = {GroupByResponse.class, SortByResponse.class})
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid datasetName or sort order parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dataset does not exist",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<Object> queryDataset(
            @Parameter(description = "Unique dataset identifier", example = "employee_dataset")
            @PathVariable String datasetName,
            @Parameter(description = "Field name (or dot-notation path) to group records by", example = "department")
            @RequestParam(required = false) String groupBy,
            @Parameter(description = "Field name (or dot-notation path) to sort records by", example = "age")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction: 'asc' (default) or 'desc'", example = "asc")
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        Object result = datasetService.queryDataset(datasetName, groupBy, sortBy, order);
        return ResponseEntity.ok(result);
    }
}
