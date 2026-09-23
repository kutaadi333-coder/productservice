# JIRA TASK 01 – REST-Based Inter-Service Communication

## Date
22-Sep-2026

## Objective
Implement and verify REST-based communication between two independent Spring Boot microservices.

## Services

| Service | Port | Purpose |
|---|---:|---|
| User Service | 8081 | Provides user information |
| Order Service | 8082 | Provides order information and calls User Service |
| Eureka Server | 8761 | Service discovery |

## Architecture

```text
Client
  |
  | GET /api/v1/orders/1
  v
Order Service :8082
  |
  | OpenFeign
  v
Eureka Server :8761
  |
  | Discover user-service
  v
User Service :8081
  |
  | User details
  v
Order Service :8082
  |
  | Combined response
  v
Client
```

## User API

### Endpoint
```text
GET /api/v1/users/{id}
```

### Example
```text
GET http://localhost:8081/api/v1/users/1
```

### Response
```json
{
  "id": 1,
  "name": "Alex Mercer",
  "email": "alex.mercer@example.com",
  "phone": "9876543210"
}
```

## Order API

### Endpoint
```text
GET /api/v1/orders/{id}
```

### Example
```text
GET http://localhost:8082/api/v1/orders/1
```

## Inter-Service Communication

Order Service uses OpenFeign to call User Service.

The Feign client is configured with:

```java
@FeignClient(
    name = "user-service",
    path = "/api/v1/users"
)
```

Eureka is used for service discovery, so Order Service can locate the User Service by its service name.

## Combined Response

After the User Service call, Order Service returns order information together with the user details.

Example:

```json
{
  "id": 1,
  "userId": 1,
  "productName": "Wireless Mouse",
  "quantity": 2,
  "amount": 1499.00,
  "status": "COMPLETED",
  "createdAt": "2026-09-08T15:39:23.905667",
  "user": {
    "id": 1,
    "name": "Alex Mercer",
    "email": "alex.mercer@example.com"
  }
}
```

## Testing

### Successful communication
Request:

```text
GET http://localhost:8082/api/v1/orders/1
```

Result:

```text
HTTP 200 OK
```

The response contained the user details returned by User Service.

### Failure scenario
User Service was stopped while Order Service remained running.

Order Service then reported that no `user-service` instance was available through service discovery.

Example log:

```text
No servers available for service: user-service
```

This confirmed the dependency between Order Service and User Service.

## Sequence Flow

```text
Client
  |
  | GET /api/v1/orders/1
  v
Order Service
  |
  | GET /api/v1/users/1 through OpenFeign
  v
User Service
  |
  | Return user details
  v
Order Service
  |
  | Combine order + user information
  v
Client
```

## Result

REST-based communication between the two independent Spring Boot services was implemented and verified successfully using OpenFeign and Eureka service discovery.

## Conclusion

Order Service can communicate with User Service through REST without directly accessing the User Service database.
