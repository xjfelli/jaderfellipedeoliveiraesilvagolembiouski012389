-- Insert sample artists
INSERT INTO artist (name, music_genre, biography, country_of_origin, photo_url, storage_id, created_at, updated_at)
VALUES
    ('The Beatles', 'Rock', 'The Beatles were an English rock band formed in Liverpool in 1960.', 'United Kingdom', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Queen', 'Rock', 'Queen are a British rock band formed in London in 1970.', 'United Kingdom', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Michael Jackson', 'Pop', 'Michael Joseph Jackson was an American singer, songwriter, and dancer.', 'United States', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Pink Floyd', 'Progressive Rock', 'Pink Floyd were an English rock band formed in London in 1965.', 'United Kingdom', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Bob Marley', 'Reggae', 'Robert Nesta Marley was a Jamaican singer, musician, and songwriter.', 'Jamaica', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample albums
INSERT INTO album (title, release_year, record_label, track_count, cover_url, storage_id, description, created_at, updated_at)
VALUES
    ('Abbey Road', 1969, 'Apple Records', 17, NULL, NULL, 'The eleventh studio album by the English rock band the Beatles.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('A Night at the Opera', 1975, 'EMI', 12, NULL, NULL, 'The fourth studio album by the British rock band Queen.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thriller', 1982, 'Epic Records', 9, NULL, NULL, 'The sixth studio album by American singer Michael Jackson.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('The Dark Side of the Moon', 1973, 'Harvest Records', 10, NULL, NULL, 'The eighth studio album by the English rock band Pink Floyd.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Legend', 1984, 'Island Records', 14, NULL, NULL, 'A compilation album by Bob Marley and the Wailers.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert artist-album relationships
INSERT INTO artist_album (artist_id, album_id)
VALUES
    (1, 1),  -- The Beatles - Abbey Road
    (2, 2),  -- Queen - A Night at the Opera
    (3, 3),  -- Michael Jackson - Thriller
    (4, 4),  -- Pink Floyd - The Dark Side of the Moon
    (5, 5);  -- Bob Marley - Legend

-- Insert sample admin user (password: admin123 - bcrypt encoded)
INSERT INTO users (username, email, password, fullname, role, is_active, created_at, updated_at)
VALUES
    ('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5p0GkJZLdXQd5FQzc1VELnT.mJrXHnW', 'Administrator', 'ROLE_ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('user', 'user@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5p0GkJZLdXQd5FQzc1VELnT.mJrXHnW', 'Regular User', 'ROLE_USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
