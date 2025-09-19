--liquibase formatted sql

--changeset openai:001-create-project
CREATE TABLE project
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(160) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE project;

--changeset openai:002-create-task
CREATE TABLE task
(
    id          UUID PRIMARY KEY,
    project_id  UUID         NOT NULL,
    title       VARCHAR(160) NOT NULL,
    description TEXT,
    is_activity BOOLEAN      NOT NULL,
    end_at      TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

CREATE INDEX idx_task_project ON task (project_id);
CREATE INDEX idx_task_end_at ON task (end_at);

--rollback DROP TABLE task;

--changeset openai:003-create-note
CREATE TABLE note
(
    id         UUID PRIMARY KEY,
    project_id UUID,
    task_id    UUID,
    body       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_note_project FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE,
    CONSTRAINT fk_note_task FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE,
    CONSTRAINT chk_note_single_target CHECK (
        (project_id IS NOT NULL AND task_id IS NULL)
            OR (project_id IS NULL AND task_id IS NOT NULL)
        )
);

CREATE INDEX idx_note_project ON note (project_id);
CREATE INDEX idx_note_task ON note (task_id);

--rollback DROP TABLE note;