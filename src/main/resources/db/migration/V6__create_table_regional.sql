CREATE TABLE regional (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE,
    external_id BIGINT
);