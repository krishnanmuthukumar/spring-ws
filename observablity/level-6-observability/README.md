# Level 6: Centralized Logging Across Services

This level introduces a small multi-service application that builds on the production logging concepts from Level 5.

The project contains three independent Spring Boot applications:

- `order-service` — accepts an order and coordinates the downstream services.
- `payment-service` — processes the payment for an order.
- `notification-service` — queues a notification after the order is created.

The services use the same Logback console format so that their output can be collected and searched by a centralized logging platform later.

## Architecture

```text
Client
  |
  | POST /orders
  v
order-service :8081
  |
  | POST /payments
  v
payment-service :8082
  |
  | POST /notifications
  v
notification-service :8083
```

The order service calls the payment service first. If payment succeeds, it calls the notification service. The order response contains the result from both downstream services.

## Correlation ID flow

The `order-service` accepts an optional `X-Correlation-ID` request header.

- If the client sends one, the value is reused.
- If the client does not send one, the order service generates a UUID.
- The value is returned in the response header.
- The same value is propagated to the payment and notification services.
- Each service places the value in SLF4J MDC and removes it after the request completes.

Example:

```text
client correlation ID: demo-123
        |
        +--> order-service         correlationId=demo-123
        +--> payment-service       correlationId=demo-123
        +--> notification-service  correlationId=demo-123
```

## Log format

Each service uses `src/main/resources/logback-spring.xml` to write consistent logs to the console:

```text
timestamp level service thread logger correlationId message
```

Example:

```text
2026-11-04T12:00:00.000+05:30 INFO service=order-service [http-nio-8081-exec-1] com.example.observability.order.OrderWorkflowService [correlationId=demo-123] - Payment completed for order ORD-1001 with status PAID
```

The services currently write to stdout. In a container or production environment, a log collector can collect stdout from all services and forward it to a centralized logging platform.

## Project structure

```text
level-6-observability/
├── order-service/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/observability/order/
│       └── resources/
│           ├── application.properties
│           └── logback-spring.xml
├── payment-service/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/observability/payment/
│       └── resources/
│           ├── application.properties
│           └── logback-spring.xml
└── notification-service/
    ├── pom.xml
    └── src/main/
        ├── java/com/example/observability/notification/
        └── resources/
            ├── application.properties
            └── logback-spring.xml
```

## Run the services

Open three terminals and start the services in this order:

### 1. Payment service

```powershell
cd observablity/level-6-observability/payment-service
mvn spring-boot:run
```

### 2. Notification service

```powershell
cd observablity/level-6-observability/notification-service
mvn spring-boot:run
```

### 3. Order service

```powershell
cd observablity/level-6-observability/order-service
mvn spring-boot:run
```

## Test the workflow

Send a request with a known correlation ID:

```powershell
Invoke-RestMethod -Method Post `
  -Headers @{ "X-Correlation-ID" = "demo-123" } `
  'http://localhost:8081/orders?orderId=ORD-1001'
```

Expected response:

```json
{
  "orderId": "ORD-1001",
  "status": "CREATED",
  "paymentStatus": "PAID",
  "notificationStatus": "QUEUED"
}
```

The response also contains:

```text
X-Correlation-ID: demo-123
```

To test automatic ID generation, omit the header:

```powershell
Invoke-RestMethod -Method Post `
  'http://localhost:8081/orders?orderId=ORD-1002'
```

## Individual service endpoints

The downstream services can also be called directly:

```powershell
Invoke-RestMethod -Method Post `
  'http://localhost:8082/payments?orderId=ORD-1001'

Invoke-RestMethod -Method Post `
  'http://localhost:8083/notifications?orderId=ORD-1001'
```

When called directly without a correlation ID, each service generates its own ID for that request.

## Build and test

Run tests for each service:

```powershell
cd observablity/level-6-observability/order-service
mvn test

cd ../payment-service
mvn test

cd ../notification-service
mvn test
```

## Learning exercises

1. Run all three services and trace one request using its correlation ID.
2. Compare the same request in the three service consoles.
3. Make the payment service fail and observe how the order service handles the downstream error.
4. Add a centralized collector such as Loki, OpenSearch, or Elasticsearch.
5. Search all collected logs by `correlationId`.
6. Add metrics for request count, error count, and downstream call duration.
7. Add distributed tracing with OpenTelemetry in Level 7.

## Scope of this level

This level focuses on consistent logs and correlation-ID propagation across service boundaries. It does not yet add:

- OpenTelemetry traces or spans;
- a metrics backend;
- a log collector or dashboard; or
- retry, timeout, and circuit-breaker policies.

Those concerns can be introduced as separate exercises after the basic request flow is understood.
