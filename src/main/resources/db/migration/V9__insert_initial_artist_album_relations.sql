INSERT INTO tb_artist_album (artist_id, album_id)
SELECT a.id, alb.id
FROM tb_artist a, tb_album alb
WHERE a.name = 'Michel Teló' AND alb.title = 'Bem Sertanejo';

INSERT INTO tb_artist_album (artist_id, album_id)
SELECT a.id, alb.id
FROM tb_artist a, tb_album alb
WHERE a.name = 'Guns N’ Roses' AND alb.title = 'Use Your Illusion I';

INSERT INTO tb_artist_album (artist_id, album_id)
SELECT a.id, alb.id
FROM tb_artist a, tb_album alb
WHERE a.name = 'Serj Tankian' AND alb.title = 'Harakiri';

INSERT INTO tb_artist_album (artist_id, album_id)
SELECT a.id, alb.id
FROM tb_artist a, tb_album alb
WHERE a.name = 'Mike Shinoda' AND alb.title = 'The Rising Tied';