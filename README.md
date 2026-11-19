# JSON Dataset Group-By & Sort-By Operator API (Spring Boot 3 / Java 17)

A production-grade, REST-compliant Spring Boot 3.4 application for persisting arbitrary JSON dataset records in a relational database and executing dynamic **Group-By** and **Sort-By** query operators.

## Architecture & Design Patterns

- **Layered Clean Architecture**: Strict separation between Controller (`DatasetController`), Service (`DatasetService` / `DatasetServiceImpl`), Domain Operators (`operator.*`), Persistence (`entity.*`, `repository.*`), DTOs (`dto.*`), and Global Exception Handling (`GlobalExceptionHandler`).
- **Strategy Pattern**: `QueryOperatorStrategy` defines a common contract implemented by `GroupByOperatorStrategy`, `SortByOperatorStrategy`, and `RawRecordsOperatorStrategy`. Adding new operators (e.g., `filterBy`, `aggregate`) requires zero changes to existing operator classes (Open/Closed Principle).
- **Factory Pattern**: `QueryOperatorFactory` dynamically resolves the active operator strategy from Spring DI beans based on the immutable `QueryContext`.
- **Relational Schema (JPA / Hibernate)**:
  - `datasets` (`id` PK, `name` UNIQUE, `created_at`)
  - `dataset_records` (`id` PK, `dataset_id` FK -> `datasets.id`, `external_record_id`, `payload_json`, `created_at`)
  - Uses H2 (file-backed relational DB) out-of-the-box for local development & testing, and includes a `prod` profile for PostgreSQL.
- **Robust Error Handling**: Centralized `@RestControllerAdvice` (`GlobalExceptionHandler`) returns structured `ApiErrorResponse` payloads for `400 Bad Request`, `404 Not Found`, malformed JSON, and unexpected `500` errors.

## Prerequisites

- Java 17+
- Maven 3.9+

## Running Locally

```bash
# 1. Run JUnit 5 + Mockito + MockMvc TDD test suite
mvn test

# 2. Start the Spring Boot server (default port 8080)
mvn spring-boot:run
```

### Running with PostgreSQL (`prod` profile)

```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/dataset_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=secret
mvn spring-boot:run
```

## Swagger / OpenAPI 3 Documentation

Once running, open:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## API Endpoints & cURL Examples

### 1. Insert Record API

```bash
curl -X POST http://localhost:8080/api/dataset/employee_dataset/record \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "name": "John Doe", "age": 30, "department": "Engineering"}'
```

Response (`201 Created`):
```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

### 2. Query API with Group-By

```bash
curl "http://localhost:8080/api/dataset/employee_dataset/query?groupBy=department"
```

Response (`200 OK`):
```json
{
  "groupedRecords": {
    "Engineering": [
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
      { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" }
    ],
    "Marketing": [
      { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" }
    ]
  }
}
```

### 3. Query API with Sort-By

```bash
curl "http://localhost:8080/api/dataset/employee_dataset/query?sortBy=age&order=asc"
```

Response (`200 OK`):
```json
{
  "sortedRecords": [
    { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" },
    { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" },
    { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" }
  ]
}
```

### 4. Combined Group-By + Sort-By (Bonus)

```bash
curl "http://localhost:8080/api/dataset/employee_dataset/query?groupBy=department&sortBy=age&order=asc"
```

## Docker Deployment

```bash
docker build -t dataset-operator-api .
docker run -p 8080:8080 dataset-operator-api
```
