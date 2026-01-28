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
    FOREIGN KEY (album_id) REFERENCES tb_album(id),
    FOREIGN KEY (artist_id) REFERENCES tb_artist(id)
);

INSERT INTO tb_artist (name) VALUES
('Serj Tankian'),
('Mike Shinoda'),
('Michel Teló'),
('Guns N’ Roses');

INSERT INTO tb_album (title) VALUES
('Harakiri'),
('The Rising Tied'),
('Bem Sertanejo'),
('Use Your Illusion I');

INSERT INTO tb_artist_album (artist_id, album_id)
VALUES (
    (SELECT id FROM tb_artist WHERE name = 'Serj Tankian'),
    (SELECT id FROM tb_album WHERE title = 'Harakiri')
);

INSERT INTO tb_artist_album (artist_id, album_id)
VALUES (
    (SELECT id FROM tb_artist WHERE name = 'Mike Shinoda'),
    (SELECT id FROM tb_album WHERE title = 'The Rising Tied')
);

INSERT INTO tb_artist_album (artist_id, album_id)
VALUES (
    (SELECT id FROM tb_artist WHERE name = 'Michel Teló'),
    (SELECT id FROM tb_album WHERE title = 'Bem Sertanejo')
);

INSERT INTO tb_artist_album (artist_id, album_id)
VALUES (
    (SELECT id FROM tb_artist WHERE name = 'Guns N’ Roses'),
    (SELECT id FROM tb_album WHERE title = 'Use Your Illusion I')
