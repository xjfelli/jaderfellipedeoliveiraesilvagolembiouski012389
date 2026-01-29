package com.gerenciadorartistas.backend.features.user.dto;

public record UserPresenterDTO(
    Long id,
    String username,
    String email,
    String fullname
) {}
