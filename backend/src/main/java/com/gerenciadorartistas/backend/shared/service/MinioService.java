package com.gerenciadorartistas.backend.shared.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(
        MinioService.class
    );

    @Autowired
    @Qualifier("internalMinioClient")
    private MinioClient minioClient;

    @Autowired
    @Qualifier("publicMinioClient")
    private MinioClient publicMinioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    /**
     * Inicializa o bucket do MinIO se não existir
     */
    @PostConstruct
    public void init() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
                logger.info("Bucket '{}' criado com sucesso", bucketName);
            } else {
                logger.info("Bucket '{}' já existe", bucketName);
            }
        } catch (Exception e) {
            logger.error(
                "Erro ao inicializar bucket do MinIO: {}",
                e.getMessage(),
                e
            );
        }
    }

    /**
     * Faz upload de um arquivo para o MinIO
     *
     * @param file      Arquivo a ser enviado
     * @param folder    Pasta onde o arquivo será armazenado (ex: "artistas", "albums")
     * @return Nome do arquivo no MinIO
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension =
                originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(
                          originalFilename.lastIndexOf(".")
                      )
                    : "";

            String fileName = folder + "/" + UUID.randomUUID() + extension;

            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            inputStream.close();
            logger.info(
                "Arquivo '{}' enviado com sucesso para o MinIO",
                fileName
            );
            return fileName;
        } catch (Exception e) {
            logger.error(
                "Erro ao fazer upload do arquivo: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException("Erro ao fazer upload do arquivo", e);
        }
    }

    /**
     * Gera uma URL pré-assinada para acesso temporário ao arquivo
     *
     * Conecta via minio:9000 e gera URLs com localhost/artistas-media/.
     * O nginx faz proxy reverso preservando Host: minio:9000, mantendo assinatura válida.
     *
     * @param fileName    Nome do arquivo no MinIO
     * @param expiryMinutes Tempo de expiração da URL em minutos
     * @return URL pré-assinada válida para acesso externo via nginx
     */
    public String getPresignedUrl(String fileName, int expiryMinutes) {
        try {
            String url = publicMinioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(expiryMinutes, TimeUnit.MINUTES)
                    .build()
            );

            // Substitui minio:9000 por localhost para acesso via nginx
            // O nginx preserva o header Host correto para validação de assinatura
            String publicUrl = url.replace("http://minio:9000", "http://localhost");

            logger.info(
                "URL pré-assinada gerada para '{}' com expiração de {} minutos: {}",
                fileName,
                expiryMinutes,
                publicUrl
            );
            return publicUrl;
        } catch (Exception e) {
            logger.error(
                "Erro ao gerar URL pré-assinada: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException("Erro ao gerar URL pré-assinada", e);
        }
    }

    /**
     * Deleta um arquivo do MinIO
     *
     * @param fileName Nome do arquivo no MinIO
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            logger.info("Arquivo '{}' deletado do MinIO", fileName);
        } catch (Exception e) {
            logger.error("Erro ao deletar arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar arquivo", e);
        }
    }

    /**
     * Verifica se um arquivo existe no MinIO
     *
     * @param fileName Nome do arquivo no MinIO
     * @return true se o arquivo existe, false caso contrário
     */
    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
