-- Migration Script: Convert character_level from VARCHAR to INT

-- 1. Add a temporary column to store the integer values
ALTER TABLE users ADD COLUMN character_level_int INT;

-- 2. Update the temporary column based on existing string values
UPDATE users SET character_level_int = 0 WHERE character_level LIKE '%일청담 다이버%';
UPDATE users SET character_level_int = 1 WHERE character_level LIKE '%술 취한 다람쥐%';
UPDATE users SET character_level_int = 2 WHERE character_level LIKE '%지갑은 지킨다%';
UPDATE users SET character_level_int = 3 WHERE character_level LIKE '%술고래 후보생%';
UPDATE users SET character_level_int = 4 WHERE character_level LIKE '%인간 알코올%';

-- Handle cases where character_level might be null or unmatched (default to 0 or keep null)
-- UPDATE users SET character_level_int = 0 WHERE character_level_int IS NULL;

-- 3. Drop the old column
ALTER TABLE users DROP COLUMN character_level;

-- 4. Rename the new column to the original name
ALTER TABLE users RENAME COLUMN character_level_int TO character_level;
