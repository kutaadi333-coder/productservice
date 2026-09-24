# Product Service — JIRA Task 05
## Swagger/OpenAPI & Professional API Documentation

### 1. Overview

The Product Service is a Spring Boot REST API for managing products. The service supports product creation, retrieval, update, partial update, deletion, pagination, sorting, filtering, search, and API versioning.

### 2. Technology Stack

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- H2 Database
- SpringDoc OpenAPI / Swagger UI

### 3. Base URL

```text
http://localhost:8083
```

### 4. Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8083/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8083/v3/api-docs
```

### 5. API Organization

#### Products

- `POST /api/v1/products` — Create a product
- `GET /api/v1/products/{id}` — Get a product by ID
- `PUT /api/v1/products/{id}` — Fully update a product
- `PATCH /api/v1/products/{id}` — Partially update a product

#### Product Search

- `GET /api/v1/products` — Pagination, sorting, filtering, and keyword search

Supported query parameters:

- `page` — Page number, starts from 0
- `size` — Number of products per page
- `sortBy` — Field used for sorting
- `sortDir` — `asc` or `desc`
- `category` — Optional category filter
- `minPrice` — Optional minimum price
- `maxPrice` — Optional maximum price
- `keyword` — Optional product-name keyword

Example:

```text
GET /api/v1/products?page=0&size=10&sortBy=price&sortDir=asc&category=Electronics&minPrice=10000&maxPrice=50000
```

#### Product Administration

- `DELETE /api/v1/products/{id}` — Delete a product

#### Product V2

- `GET /api/v2/products` — Get products with extended fields including stock and status

### 6. Request Example

```json
{
  "name": "Laptop",
  "price": 50000,
  "category": "Electronics"
}
```

### 7. V1 Response Example

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 50000,
  "category": "Electronics"
}
```

### 8. V2 Response Example

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 50000,
  "category": "Electronics",
  "stock": 0,
  "status": "AVAILABLE"
}
```

### 9. Error Handling

The service uses a standard error response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "timestamp": "2026-09-15T14:00:00"
}
```

Common responses:

- `200 OK` — Successful request
- `204 No Content` — Successful deletion
- `400 Bad Request` — Invalid request or query parameters
- `404 Not Found` — Product does not exist
- `500 Internal Server Error` — Unexpected server error

### 10. API Versioning

The service uses URL-based API versioning:

```text
/api/v1/products
/api/v2/products
```

V1 provides the existing product representation. V2 extends the response with additional product information such as `stock` and `status`, allowing the API to evolve while maintaining the V1 contract.

### 11. Running the Application

Start the Spring Boot application from IntelliJ IDEA or with Maven:

```text
mvn spring-boot:run
```

The application runs on port `8083`.

### 12. Testing

The APIs can be tested using:

1. Swagger UI
2. Postman

A Postman collection is provided separately:

```text
Product_Service_Task_05_Postman_Collection.json
```

### 13. Task 05 Deliverables

- SpringDoc OpenAPI integration
- Swagger UI
- Documented Product CRUD APIs
- Request and response examples
- Error response documentation
- API grouping
- V2 API documentation
- Postman collection
- README documentation

### 14. Notes

The H2 database is configured as an in-memory database for the current development setup. Data may be cleared when the application restarts.
