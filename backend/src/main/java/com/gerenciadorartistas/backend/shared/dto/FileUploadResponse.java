package com.gerenciadorartistas.backend.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private String fileName;
    private String presignedUrl;
    private Long fileSize;
    private String contentType;
    private String message;
}
