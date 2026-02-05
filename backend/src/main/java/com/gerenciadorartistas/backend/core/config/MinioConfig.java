package com.gerenciadorartistas.backend.core.config;

import io.minio.MinioClient;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    /**
     * MinioClient principal para operações internas (upload, delete, etc.)
     * Usa a URL interna do Docker (minio:9000)
     */
    @Bean
    @Primary
    @Qualifier("internalMinioClient")
    public MinioClient minioClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

        return MinioClient.builder()
            .endpoint(minioUrl)
            .credentials(accessKey, secretKey)
            .httpClient(httpClient)
            .build();
    }

    /**
     * MinioClient para gerar URLs pré-assinadas públicas
     *
     * IMPORTANTE: O MinIO está configurado com MINIO_DOMAIN=localhost.
     * Configuramos o cliente para usar <bucket>.localhost:9000 no endpoint,
     * o que força URLs no formato de virtual-host (bucket.localhost:9000).
     *
     * Para isso funcionar, substituímos o host na URL gerada de forma que
     * a assinatura seja compatível com localhost:9000.
     */
    @Bean
    @Qualifier("publicMinioClient")
    public MinioClient publicMinioClient() {
        // Conecta via minio:9000 para gerar assinaturas
        return MinioClient.builder()
            .endpoint(minioUrl)  // http://minio:9000
            .credentials(accessKey, secretKey)
            .build();
    }
}
