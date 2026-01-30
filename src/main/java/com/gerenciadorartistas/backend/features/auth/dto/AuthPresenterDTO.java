package com.gerenciadorartistas.backend.features.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthPresenterDTO {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String username;
    private String email;

    public AuthPresenterDTO(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String username,
        String email
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.username = username;
        this.email = email;
    }
}
