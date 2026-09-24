# Product Service — JIRA Task 04
## API Versioning & Backward Compatibility

**Service:** Product Service  
**Framework:** Spring Boot  
**Port:** 8083

### 1. Objective

Implement API versioning so that a new API contract can be introduced without breaking existing V1 clients.

### 2. Versioning Strategy

URL-based versioning is used. Each API version has a separate URL path.

```text
/api/v1/products
/api/v2/products
```

### 3. V1 API

V1 preserves the existing client contract.

Example V1 response:

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 50000
}
```

### 4. V2 API

V2 introduces additional product information while keeping the V1 API unchanged.

Example V2 response:

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 50000,
  "category": "Electronics",
  "stock": 20,
  "status": "AVAILABLE"
}
```

### 5. V1 → V2 Changes

| Field | V1 | V2 |
|---|---|---|
| `id` | Present | Present |
| `name` | Present | Present |
| `price` | Present | Present |
| `category` | Not in V1 contract | Added |
| `stock` | Not in V1 contract | Added |
| `status` | Not in V1 contract | Added |

### 6. Backward Compatibility

The existing V1 endpoint was not replaced or removed. A V2 controller and V2 response DTO were added separately.

Therefore:

- Existing clients can continue using `/api/v1/products`.
- New clients can use `/api/v2/products`.

### 7. Breaking and Non-Breaking Changes

Adding fields to a separately versioned V2 response is treated as a new API contract. The V1 response contract remains unchanged, avoiding a breaking change for V1 clients.

### 8. Migration Guide: V1 Client to V2

1. Continue using `/api/v1/products` if the client only requires the V1 fields.
2. Update the client endpoint from `/api/v1/products` to `/api/v2/products` when V2 features are required.
3. Update the client response model to include `category`, `stock`, and `status`.
4. Test the updated client against the V2 endpoint before deployment.
5. Keep V1 integration available until all dependent clients have migrated.

### 9. Testing

The following endpoints were tested independently:

```text
GET http://localhost:8083/api/v1/products
GET http://localhost:8083/api/v2/products
```

Both endpoints responded successfully. The H2 in-memory database was empty after application restart, so the successful V2 response contained an empty array.

### 10. Deliverables

- V1 API — Completed
- V2 API — Completed
- Versioning strategy — URL versioning
- Backward compatibility implementation — Completed
- API migration documentation — Completed
- Postman collection — To be prepared
- Tests — V1 and V2 endpoint verification completed

### 11. Conclusion

API versioning has been implemented using URL-based versioning. V2 is available independently of V1, and the existing V1 endpoint continues to work, providing backward compatibility for existing clients.
