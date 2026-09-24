# Product Service — JIRA Task 02
## DTOs, Request Validation & API Error Standards

**Date:** 08-Sep-2026  
**Effort:** 8 Hours  
**Priority:** High

## Objective

Build APIs that accept clean request models, validate input and return standardized error responses.

## Step 1 — Create Request DTO

Create:

```text
CreateProductRequest
```

Fields:

```text
name
description
price
category
stock
```

## Step 2 — Create Update DTO

Create:

```text
UpdateProductRequest
```

## Step 3 — Create Response DTO

Create:

```text
ProductResponse
```

## Step 4 — Stop exposing Entity directly

Controller flow:

```text
Request
 ↓
DTO
 ↓
Service
 ↓
Entity
 ↓
Database
```

Response flow:

```text
Database
 ↓
Entity
 ↓
DTO
 ↓
Response
```

## Step 5 — Add validation

Use:

```text
@NotBlank
@NotNull
@Positive
@PositiveOrZero
@Size
```

Validate:

- Product name.
- Price.
- Stock.
- Category.

## Step 6 — Test invalid input

Example:

```json
{
  "name": "",
  "price": -100,
  "stock": -5
}
```

## Step 7 — Create Global Exception Handler

Use:

```text
@RestControllerAdvice
```

Handle:

- Validation exception.
- Product not found.
- Duplicate product.
- Generic exception.

## Step 8 — Create standard error response

Example:

```json
{
  "timestamp": "2026-09-08T10:00:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Invalid product data",
  "path": "/api/v1/products"
}
```

## Step 9 — Test all error scenarios

Test:

```text
400
404
409
500
```

## Deliverables

- Request DTOs.
- Response DTOs.
- Validation.
- Global exception handler.
- Standard error response.
- Postman test evidence.
- Updated README.
