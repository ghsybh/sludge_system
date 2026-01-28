# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/sludge_system`: Spring Boot code organized by layer (`controller`, `service`, `repository`, `domain`, `dto`, `validation`, `common`).
- `src/main/resources`: application configuration (see `application.yml`).
- `src/test/java/com/sludge_system`: JUnit/Spring Boot tests.
- `db/init.sql`: MySQL schema and optional seed data.
- `target/`: build outputs (generated).
- `BACKEND_API.md`: API reference for endpoints and payloads.

## Build, Test, and Development Commands
Use the Maven wrapper so tool versions are consistent:
- `./mvnw clean package` (or `mvnw.cmd clean package`): compile and build the runnable jar.
- `./mvnw test`: run unit/integration tests.
- `./mvnw spring-boot:run`: start the API locally on port `8080`.

## Coding Style & Naming Conventions
- Java 17, Spring Boot 3.2.x; follow standard Java formatting with 4-space indentation.
- Package by layer: keep controllers in `controller`, services in `service`, repositories in `repository`.
- Naming patterns:
  - Controllers: `*Controller` (e.g., `SpectrumSampleController`)
  - Services: `*Service`
  - Repositories: `*Repository`
  - DTOs: `*Request` / `*Response`
- Prefer Lombok for boilerplate where already used; keep annotations consistent with existing classes.

## Testing Guidelines
- Framework: Spring Boot starter test (JUnit 5).
- Test naming: `*Tests.java` under `src/test/java/com/sludge_system`.
- Run tests with `./mvnw test`. No explicit coverage gate is configured.

## Commit & Pull Request Guidelines
- Git history is empty, so no established commit message convention yet.
- Suggested format: short, imperative subject (e.g., `Add sampling site validation`).
- PRs should include:
  - Summary of changes and affected endpoints.
  - Test results (command + outcome).
  - Any schema changes (update `db/init.sql` or describe migrations).

## Configuration & Data Notes
- `application.yml` targets MySQL on `localhost` with database `sludge_nir`.
- Keep credentials local; avoid committing secrets or production values.
