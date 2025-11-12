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

--changeset openai:004-add-task-duration
ALTER TABLE task
    ADD COLUMN duration INTEGER NOT NULL DEFAULT 0;

UPDATE task SET duration = 0 WHERE duration IS NULL;

ALTER TABLE task
    ALTER COLUMN duration DROP DEFAULT;

--rollback ALTER TABLE task DROP COLUMN duration;

--changeset openai:005-add-task-start-at
ALTER TABLE task
    ADD COLUMN start_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE task SET start_at = created_at WHERE start_at IS NULL;

ALTER TABLE task
    ALTER COLUMN start_at DROP DEFAULT;

CREATE INDEX idx_task_start_at ON task (start_at);

--rollback ALTER TABLE task DROP COLUMN start_at;

--changeset openai:006-add-project-start-date
ALTER TABLE project
    ADD COLUMN start_date DATE NOT NULL DEFAULT CURRENT_DATE;

UPDATE project SET start_date = created_at::DATE WHERE start_date IS NULL;

ALTER TABLE project
    ALTER COLUMN start_date DROP DEFAULT;

--rollback ALTER TABLE project DROP COLUMN start_date;

--changeset openai:007-add-task-schedule-days
ALTER TABLE task
    ADD COLUMN start_day INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN end_day   INTEGER NOT NULL DEFAULT 1;

UPDATE task t
SET start_day = GREATEST(1, (t.start_at::date - p.start_date) + 1),
    end_day   = GREATEST(start_day, (t.end_at::date - p.start_date) + 1)
FROM project p
WHERE t.project_id = p.id;

ALTER TABLE task
    ALTER COLUMN start_day DROP DEFAULT,
    ALTER COLUMN end_day DROP DEFAULT;

--rollback ALTER TABLE task DROP COLUMN start_day, DROP COLUMN end_day;

--changeset openai:008-create-tag
CREATE TABLE tag
(
    id          UUID PRIMARY KEY,
    project_id  UUID         NOT NULL,
    title       VARCHAR(160) NOT NULL,
    description TEXT,
    is_activity BOOLEAN      NOT NULL,
    end_at      TIMESTAMPTZ  NOT NULL,
    duration    INTEGER      NOT NULL,
    start_at    TIMESTAMPTZ  NOT NULL,
    start_day   INTEGER      NOT NULL,
    end_day     INTEGER      NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tag_project FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

CREATE INDEX idx_tag_project ON tag (project_id);
CREATE INDEX idx_tag_start_at ON tag (start_at);
CREATE INDEX idx_tag_end_at ON tag (end_at);

--rollback DROP TABLE tag;

--changeset openai:009-add-task-tag-color
ALTER TABLE task
    ADD COLUMN color VARCHAR(32);

ALTER TABLE tag
    ADD COLUMN color VARCHAR(32);

--rollback ALTER TABLE tag DROP COLUMN color;
--rollback ALTER TABLE task DROP COLUMN color;

--changeset openai:010-create-action
CREATE TABLE action
(
    id         UUID PRIMARY KEY,
    task_id    UUID        NOT NULL,
    details    TEXT        NOT NULL,
    day        INTEGER     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_action_task FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE
);

CREATE INDEX idx_action_task ON action (task_id);
CREATE INDEX idx_action_day ON action (day);

--rollback DROP TABLE action;
