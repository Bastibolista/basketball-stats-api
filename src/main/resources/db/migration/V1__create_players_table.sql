CREATE TABLE players (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    dominant_hand VARCHAR(10) NOT NULL,
    height_cm SMALLINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
