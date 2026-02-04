CREATE TABLE tb_artist_album (
    album_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, artist_id),
    CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES tb_album(id),
    CONSTRAINT fk_artist FOREIGN KEY (artist_id) REFERENCES tb_artist(id)
);