package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.config.health;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("MinioStorage")
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final String BUCKET_NAME = "music-covers";

    public MinioHealthIndicator(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public Health health() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );

            if (exists) {
                return Health.up()
                        .withDetail("bucket", BUCKET_NAME)
                        .withDetail("status", "Conectado e funcional")
                        .build();
            } else {
                return Health.down()
                        .withDetail("bucket", BUCKET_NAME)
                        .withDetail("erro", "Bucket não encontrado")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withException(e)
                    .withDetail("ajuda", "Verifique se o container Minio está rodando na porta 9000")
                    .build();
        }
    }
}