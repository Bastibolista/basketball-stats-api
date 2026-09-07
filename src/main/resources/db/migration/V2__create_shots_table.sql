CREATE TABLE shots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    player_id UUID NOT NULL REFERENCES players(id),
    pos_x NUMERIC(5,2) NOT NULL,
    pos_y NUMERIC(5,2) NOT NULL,
    zone VARCHAR(30) NOT NULL,
    made BOOLEAN NOT NULL,
    points SMALLINT NOT NULL,
    taken_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_shots_player_id ON shots(player_id);
