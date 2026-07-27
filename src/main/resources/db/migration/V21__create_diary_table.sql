CREATE TABLE diary (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL,
    date           DATE         NOT NULL,
    content        VARCHAR(200) DEFAULT NULL,
    character_type ENUM('BINI', 'PANI') NOT NULL,
    emotion_type   ENUM(
        'HAPPY',
        'NEUTRAL',
        'ANGRY',
        'CONFUSED',
        'KISS',
        'CRY'
    ) NOT NULL,
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     DATETIME(6)  DEFAULT NULL,
    deleted_at     DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_diary_user      FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    CONSTRAINT uq_diary_user_date UNIQUE (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
