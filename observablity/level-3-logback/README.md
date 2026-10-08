# Level 3: Logback

This Maven project builds on the SLF4J exercise and explores Logback configuration, a custom appender, and a custom encoder. The next planned experiment is filtering sensitive data before it is written to the output.

## Project Files

| File | Purpose |
| --- | --- |
| `src/main/java/com/example/logging/Application.java` | Emits INFO, WARN, and ERROR events, including a parameterized message and an error with an exception. |
| `src/main/java/com/example/logging/MyAppender.java` | Custom appender that passes each event to `MyEncoder` and writes the encoded bytes to the console. |
| `src/main/java/com/example/logging/MyEncoder.java` | Formats each event with a `MY-ENCODER` prefix and provides header and footer bytes. |
| `src/main/resources/logback.xml` | Configures the custom appender and encoder, attaches the appender to the INFO-level root logger, and registers a shutdown hook. |
| `pom.xml` | Defines the Java 11 Maven project and its SLF4J and Logback dependencies. |

## Mental Model

```text
Application
    -> SLF4J Logger API
    -> Logback creates and filters a logging event
    -> MyAppender receives the event
    -> MyEncoder formats the event as bytes
    -> MyAppender writes the bytes to the console
```

The root logger accepts INFO, WARN, and ERROR events. Events below INFO are filtered before they reach the appender. The appender delegates formatting to the encoder, keeping event routing and output formatting in separate components.

## Experiment 1: Route events through a custom appender

`MyAppender` extends Logback's `AppenderBase` and is attached to the root logger in `logback.xml`. Logback creates the appender from the XML configuration and supplies its nested encoder through `setEncoder`. The appender receives accepted events and sends them to the encoder.

## Experiment 2: Format events with a custom encoder

`MyEncoder` extends `EncoderBase<ILoggingEvent>`. Its `encode` method includes the event level, logger name, and formatted message in each output line. For example, the parameterized message `Order {}` with the argument `ORD-123` is encoded as `Order ORD-123`.

The encoder also supplies header and footer bytes. `MyAppender` writes the header when it starts and the footer when it stops. The footer therefore appears only when Logback stops the appender, such as when its logger context is stopped. A JVM shutdown hook is configured to stop Logback when the standalone process exits.

Run the application from this directory:

```powershell
mvn exec:java '-Dexec.mainClass=com.example.logging.Application'
```

Example event output:

```text
MY-ENCODER | INFO | com.example.logging.Application | Application started
MY-ENCODER | INFO | com.example.logging.Application | Order ORD-123 created
MY-ENCODER | WARN | com.example.logging.Application | This is a warning
MY-ENCODER | ERROR | com.example.logging.Application | Something went wrong
MY-ENCODER | ERROR | com.example.logging.Application | Order processing failed
```

The encoder currently formats the message text, not the attached exception details. The final event includes a `RuntimeException`, but the custom encoder does not output its type or stack trace.

## Next Experiment: Filter sensitive data

Add a Logback filter that detects sensitive values in logging events and prevents them from being written, or masks them before output. Exercise it with representative messages containing sensitive data and verify the resulting logs do not expose the original values. The filtering stage should run before the appender writes the encoded event.
