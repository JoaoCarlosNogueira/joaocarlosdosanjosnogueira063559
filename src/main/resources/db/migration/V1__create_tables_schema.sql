CREATE TABLE _user (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50)
);

CREATE TABLE regional (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE,
    external_id BIGINT
);

CREATE TABLE tb_artist (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE tb_album (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    cover_url VARCHAR(255),
    cover_image_id VARCHAR(255)
);

CREATE TABLE tb_artist_album (
    album_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, artist_id),
    CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES tb_album(id),
    CONSTRAINT fk_artist FOREIGN KEY (artist_id) REFERENCES tb_artist(id)
);