# JIRA TASK 02 – Timeout, Retry & Failure Handling

## Date
22-Sep-2026

## Priority
Critical

## Objective
Handle situations where the dependent User Service is slow or temporarily unavailable.

## Communication

```text
Order Service :8082
        |
        | OpenFeign
        v
User Service :8081
```

## 1. Timeout Configuration

Timeouts were configured for the `user-service` Feign client.

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          user-service:
            connectTimeout: 3000
            readTimeout: 5000
```

### Timeout values

| Timeout | Value | Meaning |
|---|---:|---|
| Connection timeout | 3 seconds | Maximum time allowed to establish the connection |
| Read timeout | 5 seconds | Maximum time allowed to wait for the response |

The timeout prevents Order Service from waiting indefinitely for User Service.

## 2. Timeout Testing

For testing only, User Service was intentionally delayed by 10 seconds using:

```java
Thread.sleep(10000);
```

Order Service had a 5-second read timeout.

### Result

Request:

```text
GET http://localhost:8082/api/v1/orders/1
```

Returned a controlled timeout response containing:

```text
Read timed out executing GET http://user-service/api/v1/users/1
```

This verified that Order Service did not wait indefinitely.

The temporary delay was removed after testing.

## 3. Retry Implementation

A dedicated Feign retry configuration was added:

```text
UserServiceFeignConfig
```

Configured policy:

```text
Maximum attempts: 3
Initial delay: 1 second
Maximum delay: 3 seconds
```

The `UserClient` was connected to this configuration.

Example:

```java
@FeignClient(
    name = "user-service",
    path = "/api/v1/users",
    configuration = UserServiceFeignConfig.class,
    fallback = UserClientFallback.class
)
```

## 4. Retry Policy

### Retryable failures
Transient Feign failures such as the tested read timeout are retried according to the configured retry policy.

### Non-retryable failures
Normal application HTTP errors such as `400`, `401`, and `404` are not intended to be retried by this retry configuration.

### Retry limit
Retries are limited to 3 attempts. Infinite retries are not allowed.

## 5. Retry Testing

User Service was intentionally delayed by 10 seconds.

Order Service had a 5-second read timeout.

The Order Service log confirmed three retry failures:

```text
Retry attempt for User Service failed: Read timed out executing GET http://user-service/api/v1/users/1
Retry attempt for User Service failed: Read timed out executing GET http://user-service/api/v1/users/1
Retry attempt for User Service failed: Read timed out executing GET http://user-service/api/v1/users/1
```

This verified that the configured retry limit was applied.

## 6. Final Failure Handling

After retries are exhausted, the Feign fallback throws a controlled runtime exception.

`GlobalExceptionHandler` converts the exception into a controlled JSON error response.

Example:

```json
{
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "User Service is unavailable after maximum retry attempts for user id: 1"
}
```

When User Service was stopped completely, the failure was also handled through the global exception handler.

## 7. Logging

Logging was added for the main failure-handling stages.

### Request started

`OrderController` logs the beginning of the request:

```text
Request started: Get order id=1
```

### Retry failure

`UserServiceFeignConfig` logs each retry failure:

```text
Retry attempt for User Service failed: ...
```

### Final failure

`GlobalExceptionHandler` logs the final failure:

```text
Final failure: request failed for path=/api/v1/orders/1, message=...
```

No passwords, tokens, or other secrets are intentionally logged.

## 8. Failure Flow

```text
Client
  |
  v
Order Service
  |
  | Call User Service
  v
Timeout / Temporary Failure
  |
  v
Retry 1
  |
  v
Retry 2
  |
  v
Retry 3
  |
  v
Final Failure
  |
  v
GlobalExceptionHandler
  |
  v
Controlled Error Response
```

## 9. Test Scenarios

| Scenario | Expected result | Result |
|---|---|---|
| User Service responds normally | 200 OK | Verified |
| User Service delayed beyond 5 seconds | Read timeout | Verified |
| Temporary timeout failure | Up to 3 retries | Verified |
| User Service unavailable | Controlled final error | Verified |
| Retry logging | Retry failure entries in logs | Verified |
| Final failure logging | Final failure entry in logs | Verified |

## Result

Timeout, retry, failure handling, and logging were implemented and tested for Order Service → User Service communication.

## Conclusion

The Order Service now has a bounded timeout and retry policy for User Service calls. Temporary failures are retried a limited number of times, and final failures are handled through a controlled error response and logging.
