# Project Creation and Import API Usage

## 1. Create a New Project

**Endpoint:** `POST /api/projects`

**Request Body:**
```json
{
    "name": "My New Project",
    "description": "This is a new project created from scratch",
    "startDate": "15-11-2025"
}
```

**Response:**
```json
{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "My New Project",
    "description": "This is a new project created from scratch",
    "startDate": "15-11-2025",
    "createdAt": "2025-11-13T10:30:00Z",
    "updatedAt": "2025-11-13T10:30:00Z"
}
```

## 2. Import an Existing Project

**Endpoint:** `POST /api/projects/import`

**Request Body:**
```json
{
    "sourceProjectId": "987fcdeb-51a2-45b3-c789-426614174111",
    "newProjectName": "Imported Project Copy",
    "description": "This project was imported from an existing one",
    "importTasks": true,
    "importNotes": true,
    "importTags": true
}
```

**Response:**
```json
{
    "newProjectId": "456e7890-e12b-34c5-d678-426614174222",
    "newProjectName": "Imported Project Copy",
    "importedTasksCount": 15,
    "importedNotesCount": 8,
    "importedTagsCount": 3,
    "message": "Successfully imported project 'Imported Project Copy' with 15 tasks, 8 notes, and 3 tags"
}
```

## Usage Examples

### Frontend JavaScript Example

```javascript
// Create a new project
async function createProject(projectData) {
    const response = await fetch('/api/projects', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(projectData)
    });
    return await response.json();
}

// Import an existing project
async function importProject(importData) {
    const response = await fetch('/api/projects/import', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(importData)
    });
    return await response.json();
}

// Example usage
const newProject = await createProject({
    name: "Q4 Marketing Campaign",
    description: "Marketing initiatives for Q4 2025",
    startDate: "01-10-2025"
});

const importedProject = await importProject({
    sourceProjectId: "existing-project-uuid",
    newProjectName: "Q4 Marketing Campaign - Copy",
    description: "Copy of Q4 marketing project for testing",
    importTasks: true,
    importNotes: false,
    importTags: true
});
```

### cURL Examples

```bash
# Create a new project
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Project",
    "description": "A brand new project",
    "startDate": "15-11-2025"
  }'

# Import a project
curl -X POST http://localhost:8080/api/projects/import \
  -H "Content-Type: application/json" \
  -d '{
    "sourceProjectId": "987fcdeb-51a2-45b3-c789-426614174111",
    "newProjectName": "Imported Project",
    "description": "Imported from existing project",
    "importTasks": true,
    "importNotes": true,
    "importTags": false
  }'
```

## Import Options

The import functionality allows selective importing:

- **importTasks**: Set to `true` to copy all tasks from the source project
- **importNotes**: Set to `true` to copy all project-level notes (task-specific notes are not imported)
- **importTags**: Set to `true` to copy all tags from the source project

## Error Handling

### Common Error Responses:

**400 Bad Request** - When project name already exists:
```json
{
    "message": "Project name already exists",
    "status": 400
}
```

**404 Not Found** - When source project doesn't exist:
```json
{
    "message": "Source project not found", 
    "status": 404
}
```

## Notes

1. When importing a project, the new project will have today's date as the start date
2. All timestamps (createdAt, updatedAt) for imported items will be set to the current time
3. The import is transactional - if any part fails, the entire import is rolled back
4. Project names must be unique across the system
5. The import preserves all task/tag/note properties except for IDs and timestamps