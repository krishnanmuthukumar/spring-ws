# Phase 11: Spring Boot

## Purpose and outcomes

Understand how Spring Boot simplifies setup through auto-configuration, starters, embedded Tomcat, externalized configuration, profiles, Actuator, and executable JARs.

## Place and prerequisites

Requires Phase 10. Use a pinned Spring Boot 3.x release compatible with Java 21. Boot builds on Spring; it does not replace Spring.

## Topics and startup flow

`@SpringBootApplication`; conditional auto-configuration; starters; ApplicationContext bootstrap; properties/profiles; executable JAR; command-line configuration; Actuator; diagnostics; JAR versus WAR.

```mermaid
flowchart LR
    Main[main()] --> Boot[SpringApplication.run]
    Boot --> Context[ApplicationContext]
    Boot --> Auto[Conditional auto-configuration]
    Context --> Tomcat[Embedded Tomcat]
    Context --> MVC[Spring MVC beans]
    Context --> Actuator[Actuator]
```

## Exercises

### 1. Create and migrate

- Objective: run Spring MVC as Boot.
- Tasks: create Boot Maven project; move controllers/services; start embedded Tomcat; inspect composed annotations.
- Acceptance: [ ] `mvn spring-boot:run` starts; [ ] endpoint behavior is preserved.

### 2. Inspect auto-configuration

- Objective: learn what Boot contributes.
- Tasks: inspect starters, dependency management, condition evaluation, and matched/missing conditions.
- Acceptance: [ ] explain at least three auto-configurations.

### 3. Profiles and packaging

- Objective: make startup environment-aware.
- Tasks: add properties; create development/production profiles; build executable JAR; pass a command-line override; compare WAR/JAR deployment.
- Acceptance: [ ] `java -jar` starts; [ ] profile choice is observable; [ ] comparison is documented.

### 4. Actuator

- Objective: expose basic operational information.
- Tasks: add Actuator; inspect health/info; document endpoint exposure and sensitive settings.
- Acceptance: [ ] health endpoint works; [ ] secrets are external.

## Debugging, mistakes, and checklist

Use condition reports and startup logs. Avoid floating versions, exposing all Actuator endpoints, and confusing embedded with external Tomcat.

- [ ] Standalone Boot app.
- [ ] Auto-configuration notes.
- [ ] Profiles and executable JAR.
- [ ] WAR/JAR comparison and Actuator check.

## Transition

Phase 12 evolves the web app into a documented, validated, secured, tested, and containerized REST backend.

## Official references

- [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
