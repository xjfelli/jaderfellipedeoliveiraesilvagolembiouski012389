INSERT INTO artista (nome, genero_musical, pais_origem, data_criacao, data_atualizacao) VALUES
('Serj Tankian', 'Rock/Metal', 'Líbano', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mike Shinoda', 'Rock/Hip-Hop', 'Estados Unidos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Michel Teló', 'Sertanejo', 'Brasil', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO album (titulo, ano_lancamento, data_criacao, data_atualizacao) VALUES

('Harakiri', 2012, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Black Blooms', 2024, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Rough Dog', 2009, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('The Rising Tied', 2005, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Post Traumatic', 2018, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Post Traumatic EP', 2018, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Where''d You Go', 2007, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Bem Sertanejo', 2013, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bem Sertanejo - O Show (Ao Vivo)', 2014, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bem Sertanejo - (1ª Temporada) - EP', 2013, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Serj Tankian' AND al.titulo IN ('Harakiri', 'Black Blooms', 'The Rough Dog');

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Mike Shinoda' AND al.titulo IN ('The Rising Tied', 'Post Traumatic', 'Post Traumatic EP', 'Where''d You Go');

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Michel Teló' AND al.titulo IN ('Bem Sertanejo', 'Bem Sertanejo - O Show (Ao Vivo)', 'Bem Sertanejo - (1ª Temporada) - EP');
