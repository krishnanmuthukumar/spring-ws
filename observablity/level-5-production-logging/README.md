# Level 5: Production Logging

This level turns the Spring Boot logging example from Level 4 into a production-ready logging setup. The focus is not simply producing more logs; it is producing logs that are useful for troubleshooting, safe to store, and easy to search.

The examples build on [`level-4-spring-boot`](../level-4-spring-boot/README.md), especially its `OrderController`, `OrderService`, and `logback-spring.xml` configuration.

## Base project

The directory contains a runnable starter application for the exercises:

- `CorrelationIdFilter` accepts a safe `X-Correlation-ID` header or creates a UUID.
- The filter puts the ID in MDC, returns it in the response, and clears it after the request.
- `OrderService` logs a successful order and simulates a dependency failure for IDs beginning with `FAIL-`.
- `GlobalExceptionHandler` logs the server-side exception but returns a safe error message to the client.
- `logback-spring.xml` reads the file locations from `application.properties` and owns the rolling-file policy.

Run it from this directory:

```powershell
mvn spring-boot:run
```

The service uses port `8081` so it can run beside Level 4 on port `8080`.

Create a successful order:

```powershell
Invoke-RestMethod -Method Post `
  -Headers @{ "X-Correlation-ID" = "demo-123" } `
  'http://localhost:8081/orders?orderId=ORD-1001'
```

Exercise the failure path:

```powershell
Invoke-RestMethod -Method Post `
  -Headers @{ "X-Correlation-ID" = "failure-123" } `
  'http://localhost:8081/orders?orderId=FAIL-1001'
```

The second request returns a safe error response. Search the console or `logs/order-service.log` for `failure-123` to find the complete request trail and stack trace.

## Learning goals

By the end of this level, you should be able to:

- trace one HTTP request across controller and service logs;
- emit machine-readable structured logs;
- log exceptions with useful context without duplicating stack traces;
- prevent passwords, tokens, and other secrets from reaching log files;
- configure different logging behavior for development and production; and
- apply sensible retention, rotation, and volume rules.

## Exercise 1: Add a correlation ID with MDC

Use SLF4J's Mapped Diagnostic Context (MDC) to attach a request identifier to every log event.

Create a servlet filter such as `CorrelationIdFilter`:

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader(HEADER, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

Add the value to the pattern in `logback-spring.xml`:

```xml
<property name="LOG_PATTERN"
          value="%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-5level [%thread] %logger{36} [correlationId=%X{correlationId}] - %msg%n"/>
```

Call the endpoint with a known ID:

```powershell
Invoke-RestMethod -Method Post `
  -Headers @{ "X-Correlation-ID" = "demo-123" } `
  'http://localhost:8080/orders?orderId=ORD-1001'
```

Expected log output should contain the same ID in both the controller and service messages:

```text
INFO ... [correlationId=demo-123] - Received request to create order ORD-1001
INFO ... [correlationId=demo-123] - Processing order ORD-1001
```

The `finally` block is essential. A servlet thread can handle many requests over its lifetime; leaving the MDC value behind can attach one user's request ID to another user's logs.

## Exercise 2: Log failures with context

Add a failure path to the service and log the exception as a throwable:

```java
try {
    paymentClient.charge(orderId);
} catch (PaymentException ex) {
    logger.error("Payment failed for order {}", orderId, ex);
    throw ex;
}
```

The exception should be passed separately from the message so Logback writes the stack trace. Do not log the same exception at every layer. A lower layer can add technical context, while the boundary that handles the error should normally produce the final `ERROR` event.

## Exercise 3: Add a global exception handler

Use `@RestControllerAdvice` to return a consistent error response while keeping internal details in the logs:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<String> handleNotFound(OrderNotFoundException ex) {
        logger.warn("Order lookup failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleUnexpected(Exception ex) {
        logger.error("Unexpected request failure", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Request could not be completed");
    }
}
```

The response must not expose stack traces, SQL statements, tokens, or other internal implementation details.

## Exercise 4: Protect sensitive data

Never log credentials or complete payment details:

```java
// Unsafe
logger.info("Login request username={} password={}", username, password);

// Safer
logger.info("Login request username={}", username);
```

If a value must be present for troubleshooting, mask it before logging:

```java
logger.debug("Calling payment provider with card={}", maskCardNumber(cardNumber));
```

Test that passwords, tokens, authorization headers, and card numbers do not appear in either the console output or the rolling log files. Logging filters are a safety net, not a substitute for avoiding sensitive values at the logging call site.

## Exercise 5: Use environment-specific configuration

Keep production settings outside the application artifact when possible:

```properties
# application.properties
logging.level.root=INFO
logging.level.com.example.observability=INFO
logging.file.name=logs/order-service.log
```

```properties
# application-dev.properties
logging.level.com.example.observability=DEBUG
```

Run with the development profile only when debugging locally:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Production should normally use `INFO` by default. Enable `DEBUG` temporarily and narrowly because verbose logs increase cost, storage usage, and the chance of exposing data.

## Exercise 6: Validate rotation and retention

Use the existing rolling appender from Level 4 and verify that:

- the active log is written to `logs/order-service.log`;
- files roll when the size or time threshold is reached;
- old files are removed according to `maxHistory`; and
- `totalSizeCap` prevents unbounded disk usage.

For a safe local test, temporarily reduce `maxFileSize`, generate many requests, and then restore the production value. Do not disable retention limits in a real environment.

## Production logging checklist

Before considering this level complete, verify:

- every request has a correlation ID;
- the correlation ID appears in the response and all related logs;
- MDC is cleared after each request;
- exceptions include stack traces only where they are handled;
- client responses do not reveal internal details;
- credentials and personal data are not logged;
- production defaults to an appropriate log level; and
- file rotation and retention have been tested.

## Completion challenge

Implement a failing order request that travels through the controller and service. Find every related log entry using its correlation ID, inspect the exception stack trace, confirm the response contains no internal details, and prove that no secret value is present in the generated log file.
