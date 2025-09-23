# Task Management Backend

This service provides project, task and note management APIs built with Spring Boot.

## Logging and observability

- By default Logback writes structured JSON logs to `logs/app.log` (relative to the working directory) while keeping the colourised console output for local debugging.
- Override the location by setting the `LOGGING_FILE_NAME` environment variable if you need a different target path (for example `/var/log/task-management/app.log`). Be sure that the chosen directory already exists and is writable by the process owner before starting the application.
- Both Docker Compose definitions bind mount the repository's `logs/` directory to `/app/logs` (which keeps the default `logs/app.log` target inside the container), so the host gets a rolling set of files such as `logs/app.log` and `logs/app.log.2024-05-20.0.gz`.

### Scraping the logs with Promtail

Point Promtail at the mounted directory on the host. A minimal scrape configuration looks like:

```
scrape_configs:
  - job_name: task-management
    static_configs:
      - targets: [localhost]
        labels:
          job: task-management
          __path__: /path/to/repo/logs/app.log*
```

Replace `/path/to/repo` with the absolute path of your clone. Promtail will tail both the active file and any rotated files and forward them to Loki.

## API documentation

Interactive OpenAPI documentation is available once the application is running (the service listens on port `8002` by default):

- Swagger UI: [http://localhost:8002/swagger-ui/index.html](http://localhost:8002/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8002/v3/api-docs](http://localhost:8002/v3/api-docs)

The documentation is generated automatically from controller annotations using Springdoc.


