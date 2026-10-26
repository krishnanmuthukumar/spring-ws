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

Spring boot initilizes the logback from the xml and subsequently applies the logger levels from the application.properties. 

| Component |	Responsibility |
| --- | --- |
| application.properties	| Supplies logger-level configuration to Spring Boot. |
| logback-spring.xml	| Defines Logback configuration, including appenders and any XML-defined levels. |

### Exercise 1 - Logger levels

In `application.properties`, the root logger is set to `INFO`, while loggers in the `com.example.observability` package are set to `DEBUG`:

```properties
logging.level.root=INFO
logging.level.com.example.observability=DEBUG
```

In `logback-spring.xml`, the root logger is set to `INFO` and has console and file appenders:

```xml
<root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</root>
```

During startup, Spring Boot's logging system reads `logging.level.*` properties and applies them to the corresponding Logback loggers. The `logging.level.com.example.observability=DEBUG` property sets an explicit level on that package's logger; Logback uses the nearest explicitly configured logger level when deciding whether to enable an event. The root logger's `INFO` level is used only by loggers that do not have their own configured level, so it does not override the package logger's `DEBUG` level. By default, enabled events propagate up the logger hierarchy to the root logger's appenders, so they can appear in both the console and the log file.

### Exercise 2 - Externalized logging configuration

The `ROOT_LOG_LEVEL` environment variable is read by `logback-spring.xml` to set the root logger's level. The XML expression `${ROOT_LOG_LEVEL:-INFO}` uses the environment variable when it is set and falls back to `INFO` when it is not.

To see the root logger level control loggers that inherit their level, make sure there are no explicit `logging.level.*` settings for those loggers. For example, comment out these settings in `application.properties`:

```properties
#logging.level.root=INFO
#logging.level.com.example.observability=DEBUG
```

Then set the environment variable when launching the application. In Eclipse, open `Run > Run Configurations...`, select the Spring Boot application, and add an environment variable with name `ROOT_LOG_LEVEL` and value `DEBUG` on the **Environment** tab. Restart the application after changing the configuration.

The root logger will be set to `DEBUG`. Loggers without an explicitly configured level inherit that level, so their `DEBUG` messages can be written to the console and log file.
