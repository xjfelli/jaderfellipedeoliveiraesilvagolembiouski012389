CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano_lancamento INTEGER,
    gravadora VARCHAR(255),
    numero_faixas INTEGER,
    capa_url VARCHAR(500),
    descricao TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_album_titulo ON album(titulo);
CREATE INDEX idx_album_ano ON album(ano_lancamento);
