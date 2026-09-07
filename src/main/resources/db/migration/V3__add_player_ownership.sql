ALTER TABLE players ADD COLUMN owner_subject VARCHAR(200);

UPDATE players
SET owner_subject = 'bastian'
WHERE owner_subject IS NULL;

ALTER TABLE players ALTER COLUMN owner_subject SET NOT NULL;
CREATE INDEX idx_players_owner_subject ON players(owner_subject);