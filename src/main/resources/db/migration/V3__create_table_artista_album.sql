CREATE TABLE artista_album (
    artista_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artista_id, album_id),
    CONSTRAINT fk_artista_album_artista
        FOREIGN KEY (artista_id)
        REFERENCES artista(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artista_album_album
        FOREIGN KEY (album_id)
        REFERENCES album(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_artista_album_artista ON artista_album(artista_id);
CREATE INDEX idx_artista_album_album ON artista_album(album_id);
