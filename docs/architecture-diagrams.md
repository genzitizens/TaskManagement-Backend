# Architecture Diagrams

The following Mermaid diagrams capture the key structural views of the Task Management backend service.

## System Context

This diagram shows how the Spring Boot service interacts with downstream systems for persistence, logging, and monitoring.

```mermaid
graph TD
    Client["API Consumers\n(Web, Mobile, Integrations)"] -->|"REST over HTTP"| ApiService["Task Management API\n(Spring Boot)"]
    ApiService -->|"CRUD via Spring Data JPA"| Postgres[("PostgreSQL\nTaskDB")]
    ApiService -->|"Schema managed by"| Liquibase["Liquibase Changelog\n(db/changelog)"]
    ApiService -->|"Structured logs (Logback)"| LogFile["logs/app.log"]
    LogFile -->|"Tailed by"| Promtail["Promtail Agent"]
    Promtail --> Loki[("Loki Log Store")]
    ApiService -->|"Micrometer Actuator Metrics"| Prometheus[("Prometheus Scraper")]
```

## Core Service Components

The layered architecture within the application separates HTTP concerns, domain logic, persistence, and cross-cutting monitoring.

```mermaid
flowchart TD
    subgraph WebLayer[Web Layer]
        Controllers["REST Controllers\n(Project/Task/Note)"]
    end
    subgraph ServiceLayer[Service Layer]
        Services["Services\n(Project/Task/Note)"]
        Metrics["TaskMetrics\n(Micrometer counters)"]
    end
    subgraph PersistenceLayer[Persistence Layer]
        Mappers["MapStruct Mappers"]
        Repositories["Spring Data JPA\nRepositories"]
        Entities["JPA Entities"]
    end
    Controllers -->|"Validate @Valid, delegate"| Services
    Services -->|"Domain logic"| Mappers
    Mappers -->|"Map DTOs ↔ Entities"| Repositories
    Repositories -->|"CRUD"| PostgreSQL[(PostgreSQL)]
    Services -->|"Increment counters"| Metrics
    Controllers -->|"Translate errors"| ExceptionHandler["RestExceptionHandler"]
```

## Domain Model Overview

Projects, tasks, and notes form the core aggregate relationships stored in PostgreSQL.

```mermaid
classDiagram
    class Project {
        UUID id
        String name
        String description
        Instant createdAt
        Instant updatedAt
    }
    class Task {
        UUID id
        Project project
        String title
        String description
        boolean isActivity
        Instant endAt
        Instant createdAt
        Instant updatedAt
    }
    class Note {
        UUID id
        Project project
        Task task
        String body
        Instant createdAt
        validateTarget()
    }

    Project "1" <-- "*" Task : belongs to
    Project "1" <-- "*" Note : optional
    Task "1" <-- "*" Note : optional
```

## Deployment (Docker Compose)

The default Docker Compose stack wires the service to PostgreSQL with optional admin tooling and persistent host-mounted logs.

```mermaid
flowchart LR
    subgraph SharedNetwork[shared_network]
        SpringBoot["springboot-app\n(Task Management API)"]
        Postgres["postgres\n(PostgreSQL 15)"]
        Dbgate["dbgate\n(SQL Admin UI)"]
    end
    Postgres ---|"5432"| SpringBoot
    Dbgate ---|"HTTP 3000"| Postgres
    Volume[("../task-management-logs\n(host volume)")] --> SpringBoot
```
