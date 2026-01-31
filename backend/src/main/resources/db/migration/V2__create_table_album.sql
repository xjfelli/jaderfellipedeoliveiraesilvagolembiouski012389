-- Create album table
CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INTEGER,
    record_label VARCHAR(255),
    track_count INTEGER,
    cover_url VARCHAR(500),
    storage_id VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_album_title ON album(title);
CREATE INDEX idx_album_release_year ON album(release_year);
