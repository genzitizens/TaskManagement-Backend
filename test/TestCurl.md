# Projects
### Projects are exposed under /api/projects with create, fetch, list, update, and delete operations. Project payloads require a name (max 160 chars) and accept an optional description (max 10 000 chars); updates can change either field individually.
## Create

```
curl -i -X POST "http://localhost:8002/api/projects" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Website Redesign",
        "description": "Planning the new company site."
      }'
```

## Get by ID
```
curl -i "http://localhost:8002/api/projects/$PROJECT_ID"
```

## List (page 0, 20 per page, sorted by name ascending)
```
curl -i -G "http://localhost:8002/api/projects" \
  --data-urlencode "page=0" \
  --data-urlencode "size=20" \
  --data-urlencode "sort=name,asc"
```
## Partial update (PATCH accepts any combination of fields)
```
curl -i -X PATCH "http://localhost:8002/api/projects/$PROJECT_ID" \
  -H "Content-Type: application/json" \
  -d '{
        "description": "Updated scope after stakeholder review."
      }'
```
## Delete
```
curl -i -X DELETE "http://localhost:8002/api/projects/$PROJECT_ID"
```

# Tasks
### Task endpoints live at /api/tasks, covering create, fetch, project-scoped listing, update, and delete. Creating a task requires the parent projectId, a title (max 160 chars), optional description (max 10 000 chars), a boolean isActivity, and an ISO-8601 endAt timestamp; updates may change any of those fields selectively.
## Create (endAt must be a valid Instant, e.g., UTC timestamp)
```
curl -i -X POST "http://localhost:8002/api/tasks" \
  -H "Content-Type: application/json" \
  -d "{
        \"projectId\": \"$PROJECT_ID\",
        \"title\": \"Draft UX wireframes\",
        \"description\": \"Create desktop and mobile wireframes\",
        \"isActivity\": false,
        \"endAt\": \"2024-12-01T17:00:00Z\"
      }"
```

## Get by ID
```
curl -i "http://localhost:8002/api/tasks/$TASK_ID"
```
## List tasks inside a project
```
curl -i -G "http://localhost:8002/api/tasks" \
  --data-urlencode "projectId=$PROJECT_ID" \
  --data-urlencode "page=0" \
  --data-urlencode "size=20" \
  --data-urlencode "sort=endAt,asc"
```

## Partial update
```
curl -i -X PATCH "http://localhost:8002/api/tasks/$TASK_ID" \
  -H "Content-Type: application/json" \
  -d '{
        "isActivity": true,
        "endAt": "2024-12-05T17:00:00Z"
      }'
```
# Delete
```
curl -i -X DELETE "http://localhost:8002/api/tasks/$TASK_ID"
```

# Notes
### Notes are available at /api/notes. You must provide exactly one of projectId or taskId when creating or listing notes; sending both or neither produces a 400 error. Note payloads consist of the association plus a required body (max 20 000 chars)
## Create note linked to a project
```
curl -i -X POST "http://localhost:8002/api/notes" \
  -H "Content-Type: application/json" \
  -d "{
        \"projectId\": \"$PROJECT_ID\",
        \"body\": \"Kickoff scheduled for next Monday.\"
      }"
```
## Create note linked to a task

```
curl -i -X POST "http://localhost:8002/api/notes" \
  -H "Content-Type: application/json" \
  -d "{
        \"taskId\": \"$TASK_ID\",
        \"body\": \"Remember to attach the accessibility checklist.\"
      }"
```
## List notes for a project
```
curl -i -G "http://localhost:8002/api/notes" \
  --data-urlencode "projectId=$PROJECT_ID" \
  --data-urlencode "page=0" \
  --data-urlencode "size=20"
```

## List notes for a task
```
curl -i -G "http://localhost:8002/api/notes" \
  --data-urlencode "taskId=$TASK_ID" \
  --data-urlencode "page=0" \
  --data-urlencode "size=20"
```

## Delete a note
```
curl -i -X DELETE "http://localhost:8002/api/notes/$NOTE_ID"
```