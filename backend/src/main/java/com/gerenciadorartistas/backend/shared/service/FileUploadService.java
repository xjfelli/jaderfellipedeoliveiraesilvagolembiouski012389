package com.gerenciadorartistas.backend.shared.service;

import com.gerenciadorartistas.backend.shared.dto.UploadResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private static final int DEFAULT_PRESIGNED_URL_EXPIRY_MINUTES = 30;

    @Autowired
    private MinioService minioService;

    /**
     * Faz upload de um único arquivo para uma pasta específica
     *
     * @param file   Arquivo a ser enviado
     * @param folder Pasta de destino (ex: "artists", "albums")
     * @return UploadResult com informações do arquivo
     */
    public UploadResult uploadFile(MultipartFile file, String folder) {
        return uploadFile(file, folder, DEFAULT_PRESIGNED_URL_EXPIRY_MINUTES);
    }

    /**
     * Faz upload de um único arquivo para uma pasta específica com tempo de expiração customizado
     *
     * @param file          Arquivo a ser enviado
     * @param folder        Pasta de destino (ex: "artists", "albums")
     * @param expiryMinutes Tempo de expiração da URL pré-assinada em minutos
     * @return UploadResult com informações do arquivo
     */
    public UploadResult uploadFile(
        MultipartFile file,
        String folder,
        int expiryMinutes
    ) {
        validateFile(file);

        String filePath = minioService.uploadFile(file, folder);
        String presignedUrl = minioService.getPresignedUrl(
            filePath,
            expiryMinutes
        );

        return UploadResult.of(
            file.getOriginalFilename(),
            filePath,
            presignedUrl,
            file.getSize(),
            file.getContentType(),
            expiryMinutes
        );
    }

    /**
     * Faz upload de múltiplos arquivos para uma pasta específica
     *
     * @param files  Array de arquivos a serem enviados
     * @param folder Pasta de destino (ex: "artists", "albums")
     * @return Lista de UploadResult com informações dos arquivos
     */
    public List<UploadResult> uploadMultipleFiles(
        MultipartFile[] files,
        String folder
    ) {
        return uploadMultipleFiles(
            files,
            folder,
            DEFAULT_PRESIGNED_URL_EXPIRY_MINUTES
        );
    }

    /**
     * Faz upload de múltiplos arquivos para uma pasta específica com tempo de expiração customizado
     *
     * @param files         Array de arquivos a serem enviados
     * @param folder        Pasta de destino (ex: "artists", "albums")
     * @param expiryMinutes Tempo de expiração da URL pré-assinada em minutos
     * @return Lista de UploadResult com informações dos arquivos
     */
    public List<UploadResult> uploadMultipleFiles(
        MultipartFile[] files,
        String folder,
        int expiryMinutes
    ) {
        List<UploadResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                results.add(uploadFile(file, folder, expiryMinutes));
            }
        }

        return results;
    }

    /**
     * Gera uma nova URL pré-assinada para um arquivo existente
     *
     * @param filePath Nome/caminho do arquivo no storage
     * @return UploadResult com a nova URL pré-assinada
     */
    public UploadResult refreshPresignedUrl(String filePath) {
        return refreshPresignedUrl(
            filePath,
            DEFAULT_PRESIGNED_URL_EXPIRY_MINUTES
        );
    }

    /**
     * Gera uma nova URL pré-assinada para um arquivo existente com tempo de expiração customizado
     *
     * @param filePath      Nome/caminho do arquivo no storage
     * @param expiryMinutes Tempo de expiração da URL pré-assinada em minutos
     * @return UploadResult com a nova URL pré-assinada
     */
    public UploadResult refreshPresignedUrl(
        String filePath,
        int expiryMinutes
    ) {
        if (!minioService.fileExists(filePath)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + filePath);
        }

        String presignedUrl = minioService.getPresignedUrl(
            filePath,
            expiryMinutes
        );

        return UploadResult.of(
            extractFileName(filePath),
            filePath,
            presignedUrl,
            null,
            null,
            expiryMinutes
        );
    }

    /**
     * Deleta um arquivo do storage
     *
     * @param filePath Nome/caminho do arquivo no storage
     */
    public void deleteFile(String filePath) {
        if (minioService.fileExists(filePath)) {
            minioService.deleteFile(filePath);
        }
    }

    /**
     * Verifica se um arquivo existe no storage
     *
     * @param filePath Nome/caminho do arquivo no storage
     * @return true se o arquivo existe, false caso contrário
     */
    public boolean fileExists(String filePath) {
        return minioService.fileExists(filePath);
    }

    /**
     * Faz upload de um arquivo e retorna apenas o caminho (útil para salvar em entidades)
     *
     * @param file   Arquivo a ser enviado
     * @param folder Pasta de destino
     * @return Caminho do arquivo no storage
     */
    public String uploadAndGetPath(MultipartFile file, String folder) {
        validateFile(file);
        return minioService.uploadFile(file, folder);
    }

    /**
     * Valida o arquivo antes do upload
     *
     * @param file Arquivo a ser validado
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }
    }

    /**
     * Extrai o nome do arquivo do caminho completo
     *
     * @param filePath Caminho completo do arquivo
     * @return Nome do arquivo
     */
    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        int lastSlash = filePath.lastIndexOf('/');
        return lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
    }
}
