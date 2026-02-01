package com.gerenciadorartistas.backend.features.auth.service;

import com.gerenciadorartistas.backend.features.auth.dto.AuthPresenterDTO;
import com.gerenciadorartistas.backend.features.auth.dto.LoginRequestDTO;
import com.gerenciadorartistas.backend.features.auth.dto.RefreshTokenRequestDTO;
import com.gerenciadorartistas.backend.features.auth.security.JwtTokenProvider;
import com.gerenciadorartistas.backend.features.user.entity.UserEntity;
import com.gerenciadorartistas.backend.features.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository usuarioRepository;

    /**
     * Autentica o usuário e retorna tokens JWT
     */
    public AuthPresenterDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(
            loginRequest.getUsername()
        );

        UserEntity usuario = usuarioRepository
            .findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new AuthPresenterDTO(
            accessToken,
            refreshToken,
            tokenProvider.getExpirationMs(),
            usuario.getUsername(),
            usuario.getEmail()
        );
    }

    /**
     * Renova o access token usando o refresh token
     */
    public AuthPresenterDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        if (!tokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException(
                "Token fornecido não é um refresh token"
            );
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserEntity usuario = usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newAccessToken = tokenProvider.generateAccessToken(username);
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        return new AuthPresenterDTO(
            newAccessToken,
            newRefreshToken,
            tokenProvider.getExpirationMs(),
            usuario.getUsername(),
            usuario.getEmail()
        );
    }
}
