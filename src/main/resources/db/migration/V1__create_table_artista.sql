CREATE TABLE artista (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    genero_musical VARCHAR(100),
    biografia TEXT,
    pais_origem VARCHAR(100),
    foto_url VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_artista_nome ON artista(nome);
CREATE INDEX idx_artista_genero ON artista(genero_musical);
