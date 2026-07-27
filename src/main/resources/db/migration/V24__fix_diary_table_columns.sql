ALTER TABLE diary
    MODIFY COLUMN character_type ENUM('GREEN', 'BROWN') NOT NULL,
    MODIFY COLUMN emotion_type ENUM(
        'HAPPY',
        'ANGRY',
        'SAD',
        'SURPRISED',
        'CALM',
        'TIRED'
    ) NOT NULL;

SET @deleted_at_exists = (
    SELECT COUNT(1)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'diary'
      AND column_name = 'deleted_at'
);

SET @sql = IF(@deleted_at_exists > 0,
    'ALTER TABLE diary DROP COLUMN deleted_at',
    'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @status_exists = (
    SELECT COUNT(1)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'diary'
      AND column_name = 'status'
);

SET @sql = IF(@status_exists > 0,
    'ALTER TABLE diary DROP COLUMN status',
    'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
