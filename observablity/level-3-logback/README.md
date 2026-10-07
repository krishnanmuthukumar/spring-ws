# Level 3: Logback

This Maven project builds on the SLF4J exercise by using Logback as the logging provider and routing log events through a custom appender. The custom appender receives each event and prints selected details to the console.

## Project Files

| File | Purpose |
| --- | --- |
| `src/main/java/com/example/logging/Application.java` | Emits INFO, WARN, and ERROR log events through SLF4J, including an error with a caught exception. |
| `src/main/java/com/example/logging/MyAppender.java` | Custom Logback appender that prints logger, level, raw and formatted messages, thread, timestamp, and exception type when available. |
| `src/main/resources/logback.xml` | Registers `MyAppender` and attaches it to the root logger at INFO level. |
| `pom.xml` | Defines the Java 11 Maven project and its SLF4J and Logback dependencies. |

## Mental Model

Think of logging as a pipeline:

```text
Application
    -> SLF4J Logger API
    -> Logback creates a logging event
    -> logger level filters the event
    -> attached appender receives it
    -> appender formats or routes the output
```

The application calls methods on the SLF4J API, which is the interface used by the code. Logback is the provider that handles those calls at runtime. It creates an event containing details such as the logger name, level, message, thread, timestamp, and any attached exception. In this project, `logback.xml` sets the root logger to INFO and attaches `MyAppender`. Events below INFO are filtered out; accepted events reach the appender, which prints selected event fields to the console.

## Experiment 1: Route events through a custom appender

`Application` sends log events using the SLF4J API. Logback reads `logback.xml`, creates `MyAppender`, and attaches it to the root logger. The root logger threshold is INFO, so INFO, WARN, and ERROR events are passed to the appender. For each event, `MyAppender` prints the logger name, level, raw message template, formatted message, thread name, and timestamp.

Run from this directory:

```powershell
mvn exec:java '-Dexec.mainClass=com.example.logging.Application'
```

The output contains one custom-appender block per log event, similar to:

```text
========== CUSTOM APPENDER ==========
Logger       : com.example.logging.Application
Level        : INFO
Message      : Application started
Formatted    : Application started
Thread       : com.example.logging.Application.main()
Timestamp    : <epoch milliseconds>
=====================================
```

For parameterized messages, `Message` shows the original template and `Formatted` shows the result after SLF4J substitutes the arguments. For example, `Order {}` and `Order ORD-123`.

## Experiment 2: Include exception details in an event

`Application` throws and catches a `RuntimeException`, then passes it to the error log call:

```java
try {
    throw new RuntimeException("Database unavailable");
} catch (Exception e) {
    log.error("Order processing failed", e);
}
```

The appender checks `event.getThrowableProxy()` and prints the exception class when one is attached. The last event produces fields like:

```text
Level        : ERROR
Message      : Order processing failed
Formatted    : Order processing failed
Exception    : java.lang.RuntimeException
```

This custom output currently shows the exception type only; it does not print the exception message or stack trace. Change the root level in `logback.xml` to `WARN` to see INFO events filtered out before they reach the appender.
