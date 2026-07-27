CREATE TABLE IF NOT EXISTS todo (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    created_at   DATETIME(6)  DEFAULT NULL,
    updated_at   DATETIME(6)  DEFAULT NULL,
    content      VARCHAR(255) DEFAULT NULL,
    date         DATE         DEFAULT NULL,
    is_completed BIT(1)       NOT NULL,
    user_id      BIGINT       DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_todo_user (user_id),
    CONSTRAINT fk_todo_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
