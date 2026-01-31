package com.gerenciadorartistas.backend.features.auth.controller;

import com.gerenciadorartistas.backend.features.auth.dto.AuthPresenterDTO;
import com.gerenciadorartistas.backend.features.auth.dto.LoginRequestDTO;
import com.gerenciadorartistas.backend.features.auth.dto.RefreshTokenRequestDTO;
import com.gerenciadorartistas.backend.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/auth")
@Tag(
    name = "Autenticação",
    description = "Endpoints para autenticação e gerenciamento de tokens JWT"
)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Autentica o usuário e retorna tokens JWT"
    )
    public ResponseEntity<AuthPresenterDTO> login(
        @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        AuthPresenterDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar token",
        description = "Renova o access token usando o refresh token"
    )
    public ResponseEntity<AuthPresenterDTO> refreshToken(
        @Valid @RequestBody RefreshTokenRequestDTO request
    ) {
        AuthPresenterDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
