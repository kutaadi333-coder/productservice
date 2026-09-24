# Product Service — JIRA Task 01
## REST API Fundamentals & HTTP Standards

**Date:** 07-Sep-2026  
**Effort:** 8 Hours  
**Priority:** High

## Objective

Understand REST principles and implement properly structured REST APIs using Spring Boot.

## Step 1 — Study REST fundamentals

Topics covered:

- What is REST?
- REST vs SOAP.
- Resource-based architecture.
- Stateless communication.
- Client-server architecture.
- JSON request/response.

## Step 2 — Study HTTP methods

The API uses the standard HTTP methods:

```text
GET     → Read
POST    → Create
PUT     → Complete Update
PATCH   → Partial Update
DELETE  → Delete
```

## Step 3 — Study HTTP status codes

```text
200 → Success
201 → Created
204 → No Content
400 → Bad Request
401 → Unauthorized
403 → Forbidden
404 → Not Found
409 → Conflict
500 → Internal Server Error
```

## Step 4 — Create Product Service

```text
Product Service
Port: 8083
```

## Step 5 — Create Product Entity

Fields:

```text
id
name
description
price
category
stock
status
createdAt
updatedAt
```

## Step 6 — Create CRUD APIs

```text
POST   /api/v1/products
GET    /api/v1/products
GET    /api/v1/products/{id}
PUT    /api/v1/products/{id}
PATCH  /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

## Step 7 — Return correct HTTP status codes

The implementation should not return `200 OK` for every operation.

## Step 8 — Test every endpoint in Postman

Test scenarios:

- Successful request.
- Invalid ID.
- Missing fields.
- Update.
- Delete.
- Non-existing product.

## Step 9 — Document the API

Create a basic API documentation file.

## Deliverables

- Product Service.
- CRUD APIs.
- Correct HTTP methods.
- Correct HTTP status codes.
- Postman collection.
- API documentation.
- Git commit.
