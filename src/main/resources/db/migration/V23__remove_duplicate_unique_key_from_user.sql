SET @index_exists = (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'user'
      AND index_name = 'UKn4swgcf30j6bmtb4l4cjryuym'
);

SET @sql = IF(@index_exists > 0,
    'ALTER TABLE user DROP INDEX `UKn4swgcf30j6bmtb4l4cjryuym`',
    'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
