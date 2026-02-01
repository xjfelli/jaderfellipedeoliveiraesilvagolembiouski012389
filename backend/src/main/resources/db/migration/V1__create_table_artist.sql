-- Create artist table
CREATE TABLE artist (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    music_genre VARCHAR(100),
    biography TEXT,
    country_of_origin VARCHAR(100),
    photo_url VARCHAR(500),
    storage_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_artist_name ON artist(name);
CREATE INDEX idx_artist_music_genre ON artist(music_genre);
