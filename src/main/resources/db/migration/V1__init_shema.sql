CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    last_update_at TIMESTAMP,
    due_date DATE,
    priority VARCHAR(50),
);

CREATE TABLE tasks (
    task_id BIGINT NOT NULL,
    tag VARCHAR(255),
    CONSTRAINT fk_task_tags FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
)

CREATE TABLE task_attachments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    size BIGINT,
    uploaded_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_task FOREIGN KEY (task_id)
        REFERENCES tasks (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_task_attachment_task_id ON task_attachments(task_id);