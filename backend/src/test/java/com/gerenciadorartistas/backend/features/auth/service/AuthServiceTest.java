package com.gerenciadorartistas.backend.features.auth.service;

import com.gerenciadorartistas.backend.features.auth.dto.AuthPresenterDTO;
import com.gerenciadorartistas.backend.features.auth.dto.LoginRequestDTO;
import com.gerenciadorartistas.backend.features.auth.dto.RefreshTokenRequestDTO;
import com.gerenciadorartistas.backend.features.auth.security.JwtTokenProvider;
import com.gerenciadorartistas.backend.features.user.entity.UserEntity;
import com.gerenciadorartistas.backend.features.user.entity.UserRole;
import com.gerenciadorartistas.backend.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository usuarioRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private UserEntity testUser;
    private LoginRequestDTO loginRequest;
    private RefreshTokenRequestDTO refreshTokenRequest;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ACCESS_TOKEN = "access.token.jwt";
    private static final String REFRESH_TOKEN = "refresh.token.jwt";
    private static final String NEW_ACCESS_TOKEN = "new.access.token.jwt";
    private static final String NEW_REFRESH_TOKEN = "new.refresh.token.jwt";
    private static final Long EXPIRATION_MS = 3600000L;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setRole(UserRole.ROLE_USER);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        loginRequest = new LoginRequestDTO(TEST_USERNAME, TEST_PASSWORD);
        refreshTokenRequest = new RefreshTokenRequestDTO(REFRESH_TOKEN);
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("deve autenticar usuário e retornar tokens")
        void shouldAuthenticateUserAndReturnTokens() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenProvider.generateAccessToken(authentication)).thenReturn(ACCESS_TOKEN);
            when(tokenProvider.generateRefreshToken(TEST_USERNAME)).thenReturn(REFRESH_TOKEN);
            when(tokenProvider.getExpirationMs()).thenReturn(EXPIRATION_MS);
            when(usuarioRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            AuthPresenterDTO result = authService.login(loginRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(result.getExpiresIn()).isEqualTo(EXPIRATION_MS);
            assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
            assertThat(result.getTokenType()).isEqualTo("Bearer");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider).generateAccessToken(authentication);
            verify(tokenProvider).generateRefreshToken(TEST_USERNAME);
            verify(usuarioRepository).findByUsername(TEST_USERNAME);
        }

        @Test
        @DisplayName("deve lançar exceção quando credenciais são inválidas")
        void shouldThrowExceptionWhenCredentialsAreInvalid() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciais inválidas"));

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Credenciais inválidas");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider, never()).generateAccessToken(any(Authentication.class));
            verify(tokenProvider, never()).generateRefreshToken(anyString());
        }

        @Test
        @DisplayName("deve lançar exceção quando usuário não é encontrado")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenProvider.generateAccessToken(authentication)).thenReturn(ACCESS_TOKEN);
            when(tokenProvider.generateRefreshToken(TEST_USERNAME)).thenReturn(REFRESH_TOKEN);
            when(usuarioRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuário não encontrado");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(usuarioRepository).findByUsername(TEST_USERNAME);
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshToken {

        @Test
        @DisplayName("deve renovar tokens com refresh token válido")
        void shouldRefreshTokensWithValidRefreshToken() {
            // Arrange
            when(tokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(REFRESH_TOKEN)).thenReturn(true);
            when(tokenProvider.getUsernameFromToken(REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
            when(usuarioRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
            when(tokenProvider.generateAccessToken(TEST_USERNAME)).thenReturn(NEW_ACCESS_TOKEN);
            when(tokenProvider.generateRefreshToken(TEST_USERNAME)).thenReturn(NEW_REFRESH_TOKEN);
            when(tokenProvider.getExpirationMs()).thenReturn(EXPIRATION_MS);

            // Act
            AuthPresenterDTO result = authService.refreshToken(refreshTokenRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(NEW_ACCESS_TOKEN);
            assertThat(result.getRefreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
            assertThat(result.getExpiresIn()).isEqualTo(EXPIRATION_MS);
            assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);

            verify(tokenProvider).validateToken(REFRESH_TOKEN);
            verify(tokenProvider).isRefreshToken(REFRESH_TOKEN);
            verify(tokenProvider).getUsernameFromToken(REFRESH_TOKEN);
            verify(usuarioRepository).findByUsername(TEST_USERNAME);
        }

        @Test
        @DisplayName("deve lançar exceção quando refresh token é inválido")
        void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
            // Arrange
            when(tokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Refresh token inválido ou expirado");

            verify(tokenProvider).validateToken(REFRESH_TOKEN);
            verify(tokenProvider, never()).isRefreshToken(anyString());
            verify(tokenProvider, never()).getUsernameFromToken(anyString());
        }

        @Test
        @DisplayName("deve lançar exceção quando token não é refresh token")
        void shouldThrowExceptionWhenTokenIsNotRefreshToken() {
            // Arrange
            when(tokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(REFRESH_TOKEN)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Token fornecido não é um refresh token");

            verify(tokenProvider).validateToken(REFRESH_TOKEN);
            verify(tokenProvider).isRefreshToken(REFRESH_TOKEN);
            verify(tokenProvider, never()).getUsernameFromToken(anyString());
        }

        @Test
        @DisplayName("deve lançar exceção quando usuário não é encontrado no refresh")
        void shouldThrowExceptionWhenUserNotFoundOnRefresh() {
            // Arrange
            when(tokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(REFRESH_TOKEN)).thenReturn(true);
            when(tokenProvider.getUsernameFromToken(REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
            when(usuarioRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuário não encontrado");

            verify(tokenProvider).validateToken(REFRESH_TOKEN);
            verify(tokenProvider).isRefreshToken(REFRESH_TOKEN);
            verify(tokenProvider).getUsernameFromToken(REFRESH_TOKEN);
            verify(usuarioRepository).findByUsername(TEST_USERNAME);
        }
    }

    @Nested
    @DisplayName("integração entre login e refreshToken")
    class LoginAndRefreshTokenIntegration {

        @Test
        @DisplayName("deve permitir múltiplos logins do mesmo usuário")
        void shouldAllowMultipleLoginsFromSameUser() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenProvider.generateAccessToken(authentication)).thenReturn(ACCESS_TOKEN);
            when(tokenProvider.generateRefreshToken(TEST_USERNAME)).thenReturn(REFRESH_TOKEN);
            when(tokenProvider.getExpirationMs()).thenReturn(EXPIRATION_MS);
            when(usuarioRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            AuthPresenterDTO result1 = authService.login(loginRequest);
            AuthPresenterDTO result2 = authService.login(loginRequest);

            // Assert
            assertThat(result1).isNotNull();
            assertThat(result2).isNotNull();

            verify(authenticationManager, times(2)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }
}
