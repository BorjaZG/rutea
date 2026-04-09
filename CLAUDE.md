# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Rutea is a REST API for managing tourist routes, points of interest, reviews, and users. Built with Spring Boot 3.3 + Java 17, backed by MariaDB.

## Commands

### Build & Run
```bash
./mvnw spring-boot:run          # Run the application
./mvnw clean package            # Build JAR
./mvnw compile                  # Compile only
```

### Tests
```bash
./mvnw test                          # All tests
./mvnw test -Dtest=RutaServiceTest   # Single test class
./mvnw test -Dtest="*ServiceTest"    # All service tests
./mvnw test -Dtest="*ControllerTest" # All controller tests
```

### Database (Docker)
```bash
docker compose -f docker-compose.dev.yaml up -d   # Start MariaDB
docker compose -f docker-compose.dev.yaml down    # Stop
```
DB credentials: `rutea_user` / `rutea_password` on `localhost:3306/rutea`.

### API Docs
Swagger UI at `http://localhost:8080/swagger-ui.html` when running. Full OpenAPI spec in `rutea.yaml`.

## Architecture

**Layered architecture:** Controller → Service → Repository → Domain

### Package structure
- `domain/` — JPA entities (`Ruta`, `PuntoInteres`, `Categoria`, `Usuario`, `Resena`)
- `dto/` — Input DTOs (`*InDto`) validated with Bean Validation; Output DTOs (`*OutDto`) returned to clients
- `controller/` — `@RestController` classes, one per entity. Each controller also declares its own `@ExceptionHandler` methods for entity-specific exceptions (in addition to the global handler)
- `service/` — Business logic; uses ModelMapper for entity↔DTO conversion
- `repository/` — Spring Data JPA interfaces
- `exception/` — Custom `*NotFoundException` classes + `GlobalExceptionHandler` (`@RestControllerAdvice`) + `ErrorResponse` response wrapper

### Key relationships
- `Ruta` → `Usuario` (ManyToOne)
- `Ruta` ↔ `PuntoInteres` (ManyToMany via `ruta_puntos` join table)
- `PuntoInteres` → `Categoria` (ManyToOne)
- `PuntoInteres` → `Resena` (OneToMany)
- `Usuario` → `Resena` (OneToMany)

### PATCH implementation pattern
PATCH endpoints accept `Map<String, Object> updates` and apply fields via a `switch` statement in the service. Supported fields are explicitly listed — unknown fields are logged and ignored.

### DTO pattern for relations
Relations are not serialized as nested objects. Instead, Output DTOs expose `usuarioId`, `categoriaId`, `puntosIds` (list of IDs) — manually set in service `toOutDto()` helper methods after ModelMapper mapping, since ModelMapper does not resolve these automatically.

### Exception handling
Two-level: `GlobalExceptionHandler` catches all custom `*NotFoundException`s and common Spring exceptions (validation, type mismatch, malformed JSON). Individual controllers also declare local `@ExceptionHandler` methods that handle the same exceptions — these take precedence within that controller's scope.

### Logging
Logback configured in `src/main/resources/logback-spring.xml`. Log files written to `logs/rutea.log` with daily rolling.

### Tests
- Controller tests use `@WebMvcTest` + `MockMvc` + `@MockBean` on the service layer
- Service tests use Mockito with mocked repositories
- WireMock is on the classpath but not yet used in tests