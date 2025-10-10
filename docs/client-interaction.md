# Client Interaction Diagrams

The following diagrams describe how an external client works with the Task Management Service. They can be embedded directly in Markdown viewers that support [Mermaid](https://mermaid.js.org/) or rendered with tools such as the [Mermaid CLI](https://github.com/mermaid-js/mermaid-cli).

## Sequence diagram

```mermaid
sequenceDiagram
    participant C as Client
    participant API as Task Management API
    participant SVC as Service Layer
    participant REPO as Repository
    participant DB as PostgreSQL

    C->>API: HTTP request (create/update/read task, project, or note)
    API->>API: Validate payload & map DTOs
    API->>SVC: Delegate to business service
    SVC->>SVC: Apply business rules & compose entities
    SVC->>REPO: Persist or fetch domain entity
    REPO->>DB: Execute JPA operation
    DB-->>REPO: Return persisted or fetched data
    REPO-->>SVC: Entity / collection
    SVC-->>API: Map to response DTO
    API-->>C: HTTP response (JSON payload)
    API-->>C: Error payload (validation / domain issue)
```

## Flowchart

```mermaid
flowchart TD
    A[Client request] --> B{Is request valid?}
    B -- No --> E[Return 400 Bad Requestwith error details]
    B -- Yes --> C[Controller invokes service layer]
    C --> D{Business rules satisfied?}
    D -- No --> F[Throw domain exception handled by RestExceptionHandler]
    F --> G[Return 4xx/5xx response with structured error body]
    D -- Yes --> H[Repository performs JPA call]
    H --> I[Database stores/fetches data]
    I --> J[Repository maps entity]
    J --> K[Service maps entity to DTO]
    K --> L[Controller builds HTTP response]
    L --> M[Client receives JSON payload]
```

Both diagrams reflect the same happy-path and error handling behaviour that is implemented in the controllers, services, and repositories within this project.
