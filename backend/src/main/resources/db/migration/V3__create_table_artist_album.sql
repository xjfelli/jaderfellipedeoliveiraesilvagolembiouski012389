-- Create artist_album junction table
CREATE TABLE artist_album (
    artist_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artist_id, album_id),
    CONSTRAINT fk_artist_album_artist
        FOREIGN KEY (artist_id)
        REFERENCES artist(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artist_album_album
        FOREIGN KEY (album_id)
        REFERENCES album(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_artist_album_artist ON artist_album(artist_id);
CREATE INDEX idx_artist_album_album ON artist_album(album_id);
