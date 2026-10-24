# Level 4: Spring Boot Logging

This project builds on the earlier SLF4J and Logback exercises with a small Spring Boot order service. Spring Boot supplies SLF4J and Logback through `spring-boot-starter-web`.

## Project structure

- `OrderApplication.java` starts the Spring Boot application.
- `controller/OrderController.java` exposes `POST /orders?orderId=ORD-1001`.
- `service/OrderService.java` logs order processing and returns a confirmation.
- `config/LoggingProperties.java` binds the example `app.logging` property.
- `application.properties` configures the server, log levels, and log file.
- `logback-spring.xml` writes formatted logs to the console and a rolling file.

## Logging configuration

The project configures logging in two places:

- `src/main/resources/application.properties`
  - `logging.level.root=INFO`
  - `logging.level.com.example.observability=INFO`
  - `logging.file.name=logs/order-service.log`
- `src/main/resources/logback-spring.xml`
  - adds a `CONSOLE` appender and a rolling `FILE` appender
  - uses the format: timestamp, log level, thread, logger name, app name, message
  - writes logs to `logs/order-service.log`
  - rolls files when they reach 10 MB or daily, keeping the last 7 days and a total size cap of 100 MB
  - allows overriding the root logger via `ROOT_LOG_LEVEL`, defaulting to `INFO`

## Run

Use Java 17 or later and run from this directory:

```powershell
mvn spring-boot:run
```

Create an order from another terminal:

```powershell
Invoke-RestMethod -Method Post 'http://localhost:8080/orders?orderId=ORD-1001'
```

Logs appear in the console and in `logs/order-service.log`.
