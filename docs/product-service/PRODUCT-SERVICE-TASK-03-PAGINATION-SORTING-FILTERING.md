# Product Service — JIRA Task 03
## Pagination, Sorting & Filtering

**Service:** Product Service  
**Framework:** Spring Boot  
**Port:** 8083

### 1. Objective

Implement and verify pagination, sorting, filtering, combined search, query parameter validation, Postman testing, and performance observation for the Product Service API.

### 2. API Endpoints

```text
GET  http://localhost:8083/api/v1/products
POST http://localhost:8083/api/v1/products
```

### 3. Pagination

Pagination is implemented using Spring Data JPA `Pageable` and `PageRequest`.

Supported parameters:

- `page` — default `0`
- `size` — default `10`

Response metadata:

- `content`
- `pageNumber`
- `pageSize`
- `totalElements`
- `totalPages`
- `last`

### 4. Sorting

Dynamic sorting is supported using `sortBy` and `sortDir`.

Examples:

```text
GET /api/v1/products?page=0&size=10&sortBy=price&sortDir=asc
GET /api/v1/products?page=0&size=10&sortBy=price&sortDir=desc
```

Price ascending and descending sorting were tested successfully.

### 5. Filtering

Supported filters:

- `category`
- `minPrice`
- `maxPrice`
- `keyword`

Keyword filtering searches the product name. Category matching is case-insensitive.

### 6. Combined Search API

Multiple filters can be combined with pagination and sorting.

```text
GET /api/v1/products?category=Electronics&minPrice=15000&maxPrice=50000&page=0&size=10&sortBy=price&sortDir=asc
```

The combined request was tested successfully.

### 7. Query Parameter Validation

Validation implemented for:

- `page` must be greater than or equal to `0`
- `size` must be greater than `0`
- `minPrice` cannot be negative
- `maxPrice` cannot be negative
- `minPrice` cannot be greater than `maxPrice`

Invalid parameters return HTTP `400 Bad Request`.

Example tested:

```text
GET /api/v1/products?page=-1&size=10
```

Response:

```json
{
  "status": 400,
  "message": "Page must be greater than or equal to 0",
  "error": "Bad Request"
}
```

### 8. Postman Testing

Tested scenarios:

- Create Product
- Pagination
- Sort by price ascending
- Sort by price descending
- Filter by category
- Filter by minimum price
- Filter by maximum price
- Keyword search
- Combined filters with sorting
- Negative page validation

An importable Postman collection was prepared separately.

### 9. Performance Observation

API response time was observed using Postman's response-time measurement. An approximately 17 ms response time was observed during testing.

A rigorous large-dataset benchmark was not performed because the application currently uses an H2 in-memory database and test data is reset when the Spring Boot application restarts.

### 10. Test Data Used

| ID | Product | Price | Category |
|---:|---|---:|---|
| 1 | Laptop | 50000 | Electronics |
| 2 | Laptop | 50000 | Electronics |
| 3 | Phone | 20000 | Electronics |
| 4 | Headphones | 5000 | Accessories |
| 5 | Monitor | 15000 | Electronics |

### 11. Deliverable Status

- Pagination — Completed
- Sorting — Completed
- Filtering — Completed
- Combined Search API — Completed
- Query Parameter Validation — Completed
- Postman Collection — Completed
- Performance Comparison — Basic response-time observation completed
- Documentation — Completed

### 12. Conclusion

The Product Service supports paginated, sorted, filtered and combined product retrieval with query parameter validation. Core API scenarios were tested through Postman, and supporting Postman collection and documentation were prepared.
