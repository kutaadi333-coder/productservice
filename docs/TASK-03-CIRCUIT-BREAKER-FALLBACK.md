# JIRA TASK 03 — Circuit Breaker & Fallback

## 1. Task Overview

**Task:** Circuit Breaker & Fallback  
**Service:** Order Service  
**Dependent Service:** User Service  
**Order Service Port:** 8082  
**User Service Port:** 8081  
**Priority:** Critical  
**Effort:** 8 Hours  
**Date:** 23-Sep-2026

### Objective

Implement the Circuit Breaker pattern for communication between Order Service and User Service to prevent cascading failures when User Service becomes unavailable.

---

## 2. Architecture

The communication flow is:

```text
Client
  |
  v
Order Service (8082)
  |
  | OpenFeign + Eureka
  v
Circuit Breaker
  |
  v
User Service (8081)
```

When User Service is healthy:

```text
Client
  |
  v
Order Service
  |
  v
Circuit Breaker (CLOSED)
  |
  v
User Service
  |
  v
User Response
  |
  v
Order Service
  |
  v
Client
```

When User Service fails:

```text
Client
  |
  v
Order Service
  |
  v
Circuit Breaker
  |
  X  User Service unavailable
  |
  v
Fallback
  |
  v
Order Response with
"user": null
"message": "User service temporarily unavailable"
```

---

## 3. Circuit Breaker States

### CLOSED

The system is operating normally. Requests are allowed to User Service and failures are monitored.

### OPEN

The failure threshold has been reached. Further calls are prevented from being sent to the unavailable dependency, protecting the Order Service from repeated failures.

### HALF_OPEN

After the configured open-state wait duration, a limited number of test calls are allowed. If the dependency is healthy, the Circuit Breaker returns to CLOSED. If the test calls fail, the Circuit Breaker can return to OPEN.

---

## 4. Dependency

The Spring Cloud Circuit Breaker Resilience4j starter was added to the Order Service.

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    <version>5.0.0</version>
</dependency>
```

The application classpath confirmed that Spring Cloud Circuit Breaker and Resilience4j libraries were loaded successfully.

---

## 5. Circuit Breaker Configuration

The Circuit Breaker was configured for the actual Feign Circuit Breaker instance:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      UserClientgetUserByIdLong:
        failureRateThreshold: 50
        minimumNumberOfCalls: 5
        slidingWindowSize: 5
        slidingWindowType: COUNT_BASED
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 2
```

### Configuration Explanation

| Property | Value | Purpose |
|---|---:|---|
| failureRateThreshold | 50% | Opens the circuit when the failure rate reaches 50% or higher after the minimum call requirement |
| minimumNumberOfCalls | 5 | At least 5 calls are required before failure-rate evaluation can open the circuit |
| slidingWindowSize | 5 | The failure calculation uses the latest 5 calls |
| slidingWindowType | COUNT_BASED | The sliding window is based on number of calls |
| waitDurationInOpenState | 10s | Circuit remains OPEN for 10 seconds before allowing recovery testing |
| permittedNumberOfCallsInHalfOpenState | 2 | Two test calls are permitted in HALF_OPEN |

---

## 6. Fallback Implementation

The Feign fallback was configured for User Service failure.

Current fallback behavior:

```java
@Override
public UserDto getUserById(Long id) {

    logger.warn(
            "Circuit Breaker fallback triggered for userId={}",
            id
    );

    return null;
}
```

When the fallback is triggered, `OrderService` detects the unavailable user response and returns:

```json
{
    "user": null,
    "message": "User service temporarily unavailable"
}
```

This avoids returning fake or misleading user information.

---

## 7. API Used for Testing

### Order API

```text
GET http://localhost:8082/api/v1/orders/1
```

### Circuit Breaker Actuator API

```text
GET http://localhost:8082/actuator/circuitbreakers
```

---

## 8. Healthy State Test — CLOSED

### Test Condition

- Eureka running
- User Service running on port 8081
- Order Service running on port 8082

### Request

```text
GET http://localhost:8082/api/v1/orders/1
```

### Result

```text
HTTP 200 OK
```

The Order Service successfully retrieved the order and user information.

This verified the normal communication path while the Circuit Breaker was healthy.

---

## 9. User Service Failure Test

User Service was stopped intentionally.

Order Service logs showed:

```text
No servers available for service: user-service
Load balancer does not contain an instance for the service user-service
```

This confirmed that the dependent service was unavailable.

---

## 10. Fallback Test

With User Service stopped, the same Order API was called:

```text
GET http://localhost:8082/api/v1/orders/1
```

The response was:

```json
{
    "id": 1,
    "userId": 1,
    "productName": "Wireless Mouse",
    "quantity": 2,
    "amount": 1499.00,
    "status": "COMPLETED",
    "createdAt": "2026-09-08T15:39:23.905667",
    "user": null,
    "message": "User service temporarily unavailable"
}
```

This verified the controlled fallback response.

---

## 11. OPEN State Verification

The Circuit Breaker instance used by the Feign call was:

```text
UserClientgetUserByIdLong
```

After User Service was stopped, 5 failed calls were recorded.

Actuator response showed:

```json
{
    "bufferedCalls": 5,
    "failedCalls": 5,
    "failureRate": "100.0%",
    "failureRateThreshold": "50.0%",
    "state": "OPEN"
}
```

### Result

The Circuit Breaker transitioned:

```text
CLOSED → OPEN
```

because:

```text
Failure rate = 100%
Configured threshold = 50%
Minimum calls = 5
```

---

## 12. HALF_OPEN State Verification

User Service was restarted.

After the configured 10-second open-state wait duration, the Circuit Breaker moved to:

```text
HALF_OPEN
```

This confirmed that the Circuit Breaker allowed limited recovery test calls.

---

## 13. CLOSED State Recovery Verification

With User Service healthy again, the permitted test calls succeeded.

The Circuit Breaker returned to:

```text
CLOSED
```

This verified the complete recovery flow:

```text
CLOSED → OPEN → HALF_OPEN → CLOSED
```

---

## 14. Complete State Transition

```text
                    Failures reach threshold
               +------------------------------+
               |                              v
          +---------+                    +---------+
          | CLOSED  | -----------------> |  OPEN   |
          +---------+                    +---------+
               ^                              |
               |                              | Wait 10 seconds
               |                              v
               |                         +-----------+
               |     Successful tests    | HALF_OPEN |
               +-------------------------+-----------+
```

---

## 15. Logging

The implementation includes logging for fallback activation:

```text
Circuit Breaker fallback triggered for userId=1
```

Failure conditions also produced load-balancer warnings such as:

```text
No servers available for service: user-service
```

No database credentials or other secrets are included in the application logs used for this task.

---

## 16. Testing Summary

| Test | Condition | Result |
|---|---|---|
| Healthy request | User Service running | 200 OK |
| Failure simulation | User Service stopped | Failure detected |
| Fallback | User Service unavailable | `user: null` + unavailable message |
| OPEN state | 5 failures recorded | OPEN |
| HALF_OPEN state | Recovery wait completed | HALF_OPEN |
| Recovery | User Service healthy and test calls successful | CLOSED |

---

## 17. Final Result

JIRA Task 03 implementation and functional testing were completed.

Verified:

- Circuit Breaker dependency
- Resilience4j configuration
- User Service failure simulation
- Fallback handling
- Actuator verification
- CLOSED state
- OPEN state
- HALF_OPEN state
- Recovery to CLOSED
- Complete state transition:

```text
CLOSED → OPEN → HALF_OPEN → CLOSED
```
