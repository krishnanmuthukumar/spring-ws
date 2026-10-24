# Observability Learning Path

The exercises progress from basic application logging to production observability:

```text
main
 │
 ├── v1-basic-java-logging
 │
 ├── v2-slf4j
 │
 ├── v3-logback
 │
 ├── v4-spring-boot
 │
 ├── v5-production-logging
 │
 ├── v6-centralized-logging
 │
 └── v7-observability
```

## Log Level Hierarchy

Log levels are ordered from most verbose to most severe. A logger configured at a certain level prints messages at that level and above.

```text
ALL
  ↓
TRACE
  ↓
DEBUG
  ↓
INFO
  ↓
WARN
  ↓
ERROR
  ↓
FATAL
  ↓
OFF
```

- `TRACE` — very detailed diagnostic messages for deep troubleshooting.
- `DEBUG` — development-time information useful when debugging logic.
- `INFO` — normal application lifecycle events and status updates.
- `WARN` — unexpected conditions that do not stop the app but should be reviewed.
- `ERROR` — failures that impact functionality and need immediate attention.
- `FATAL` — severe errors that usually cause the application to terminate or fail critical work.

In short: lower levels are more verbose; higher levels are more important and less noisy.

## Current Exercise Guides

- [Level 1: Core Java Logging](level-1-core-java/README.md) — plain Java logging with `java.util.logging` (JUL).
- [Level 2: SLF4J API](level-2-slf4j/README.md) — SLF4J API, providers, log levels, and exception logging.
- [Level 3: Logback](level-3-logback/README.md) — configure Logback and inspect events with a custom appender.
