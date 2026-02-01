package com.gerenciadorartistas.backend.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorDetails error;
    private PaginationInfo pagination;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().success(true).data(data).build();
    }

    public static <T> ApiResponse<T> success(
        T data,
        PaginationInfo pagination
    ) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .pagination(pagination)
            .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(new ErrorDetails(code, message))
            .build();
    }

    public static <T> ApiResponse<T> error(
        String code,
        String message,
        String details
    ) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(new ErrorDetails(code, message, details))
            .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {

        private String code;
        private String message;
        private String details;

        public ErrorDetails(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {

        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;

        public static PaginationInfo from(
            org.springframework.data.domain.Page<?> page
        ) {
            return PaginationInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
        }
    }
}
