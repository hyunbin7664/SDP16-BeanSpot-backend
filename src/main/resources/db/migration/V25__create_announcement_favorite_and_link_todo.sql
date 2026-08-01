-- 관심 공고 (사용자가 캘린더에서 추적하고 싶은 공고)
CREATE TABLE announcement_favorite (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    user_id         BIGINT      NOT NULL,
    announcement_id BIGINT      NOT NULL,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_favorite_user         FOREIGN KEY (user_id)         REFERENCES user (id)                ON DELETE CASCADE,
    CONSTRAINT fk_favorite_announcement FOREIGN KEY (announcement_id) REFERENCES announcement_common (id) ON DELETE CASCADE,
    CONSTRAINT uq_favorite_user_announcement UNIQUE (user_id, announcement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 할 일을 공고에 연결.
-- nullable: 공고와 무관한 일반 할 일도 계속 만들 수 있어야 하므로 NOT NULL 로 두지 않음.
ALTER TABLE todo
    ADD COLUMN announcement_id BIGINT DEFAULT NULL,
    ADD CONSTRAINT fk_todo_announcement FOREIGN KEY (announcement_id) REFERENCES announcement_common (id) ON DELETE CASCADE;
