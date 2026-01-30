package com.gerenciadorartistas.backend.features.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciadorartistas.backend.features.auth.dto.AuthPresenterDTO;
import com.gerenciadorartistas.backend.features.auth.dto.LoginRequestDTO;
import com.gerenciadorartistas.backend.features.auth.dto.RefreshTokenRequestDTO;
import com.gerenciadorartistas.backend.features.auth.service.AuthService;
import com.gerenciadorartistas.backend.shared.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ACCESS_TOKEN = "access.token.jwt";
    private static final String REFRESH_TOKEN = "refresh.token.jwt";
    private static final Long EXPIRATION_MS = 3600000L;

    @RestControllerAdvice
    static class TestExceptionHandler {

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
            BadCredentialsException ex
        ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("AUTHENTICATION_ERROR", ex.getMessage())
            );
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex
        ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("RUNTIME_ERROR", ex.getMessage())
            );
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new TestExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /v1/api/auth/login")
    class LoginEndpoint {

        @Test
        @DisplayName("deve retornar 200 e tokens quando login é bem sucedido")
        void shouldReturn200AndTokensWhenLoginIsSuccessful() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                TEST_USERNAME,
                TEST_PASSWORD
            );
            AuthPresenterDTO authResponse = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRATION_MS,
                TEST_USERNAME,
                TEST_EMAIL
            );

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(
                authResponse
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN))
                .andExpect(jsonPath("$.expiresIn").value(EXPIRATION_MS))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

            verify(authService).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando username está vazio")
        void shouldReturn400WhenUsernameIsEmpty() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                "",
                TEST_PASSWORD
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando password está vazio")
        void shouldReturn400WhenPasswordIsEmpty() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                TEST_USERNAME,
                ""
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando username é null")
        void shouldReturn400WhenUsernameIsNull() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                null,
                TEST_PASSWORD
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando password é null")
        void shouldReturn400WhenPasswordIsNull() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                TEST_USERNAME,
                null
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 401 quando credenciais são inválidas")
        void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                TEST_USERNAME,
                "wrongpassword"
            );

            when(authService.login(any(LoginRequestDTO.class))).thenThrow(
                new BadCredentialsException("Credenciais inválidas")
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                    jsonPath("$.error.code").value("AUTHENTICATION_ERROR")
                )
                .andExpect(
                    jsonPath("$.error.message").value("Credenciais inválidas")
                );

            verify(authService).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando body está vazio")
        void shouldReturn400WhenBodyIsEmpty() throws Exception {
            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 415 quando content-type não é JSON")
        void shouldReturn415WhenContentTypeIsNotJson() throws Exception {
            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("username=test&password=test")
                )
                .andExpect(status().isUnsupportedMediaType());

            verify(authService, never()).login(any(LoginRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("POST /v1/api/auth/refresh")
    class RefreshTokenEndpoint {

        @Test
        @DisplayName(
            "deve retornar 200 e novos tokens quando refresh é bem sucedido"
        )
        void shouldReturn200AndNewTokensWhenRefreshIsSuccessful()
            throws Exception {
            // Arrange
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(
                REFRESH_TOKEN
            );
            String newAccessToken = "new.access.token.jwt";
            String newRefreshToken = "new.refresh.token.jwt";
            AuthPresenterDTO authResponse = new AuthPresenterDTO(
                newAccessToken,
                newRefreshToken,
                EXPIRATION_MS,
                TEST_USERNAME,
                TEST_EMAIL
            );

            when(
                authService.refreshToken(any(RefreshTokenRequestDTO.class))
            ).thenReturn(authResponse);

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(refreshRequest)
                        )
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                .andExpect(jsonPath("$.refreshToken").value(newRefreshToken))
                .andExpect(jsonPath("$.expiresIn").value(EXPIRATION_MS))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

            verify(authService).refreshToken(any(RefreshTokenRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando refreshToken está vazio")
        void shouldReturn400WhenRefreshTokenIsEmpty() throws Exception {
            // Arrange
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(
                ""
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(refreshRequest)
                        )
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).refreshToken(
                any(RefreshTokenRequestDTO.class)
            );
        }

        @Test
        @DisplayName("deve retornar 400 quando refreshToken é null")
        void shouldReturn400WhenRefreshTokenIsNull() throws Exception {
            // Arrange
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(
                null
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(refreshRequest)
                        )
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).refreshToken(
                any(RefreshTokenRequestDTO.class)
            );
        }

        @Test
        @DisplayName("deve retornar 500 quando refresh token é inválido")
        void shouldReturn500WhenRefreshTokenIsInvalid() throws Exception {
            // Arrange
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(
                "invalid.refresh.token"
            );

            when(
                authService.refreshToken(any(RefreshTokenRequestDTO.class))
            ).thenThrow(
                new RuntimeException("Refresh token inválido ou expirado")
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(refreshRequest)
                        )
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("RUNTIME_ERROR"))
                .andExpect(
                    jsonPath("$.error.message").value(
                        "Refresh token inválido ou expirado"
                    )
                );

            verify(authService).refreshToken(any(RefreshTokenRequestDTO.class));
        }

        @Test
        @DisplayName(
            "deve retornar 500 quando access token é usado como refresh token"
        )
        void shouldReturn500WhenAccessTokenIsUsedAsRefreshToken()
            throws Exception {
            // Arrange
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(
                ACCESS_TOKEN
            );

            when(
                authService.refreshToken(any(RefreshTokenRequestDTO.class))
            ).thenThrow(
                new RuntimeException("Token fornecido não é um refresh token")
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(refreshRequest)
                        )
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("RUNTIME_ERROR"))
                .andExpect(
                    jsonPath("$.error.message").value(
                        "Token fornecido não é um refresh token"
                    )
                );

            verify(authService).refreshToken(any(RefreshTokenRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando body está vazio")
        void shouldReturn400WhenBodyIsEmpty() throws Exception {
            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest());

            verify(authService, never()).refreshToken(
                any(RefreshTokenRequestDTO.class)
            );
        }
    }

    @Nested
    @DisplayName("validações de entrada")
    class InputValidations {

        @Test
        @DisplayName("deve aceitar username com caracteres especiais")
        void shouldAcceptUsernameWithSpecialCharacters() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                "user@domain.com",
                TEST_PASSWORD
            );
            AuthPresenterDTO authResponse = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRATION_MS,
                "user@domain.com",
                TEST_EMAIL
            );

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(
                authResponse
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk());

            verify(authService).login(any(LoginRequestDTO.class));
        }

        @Test
        @DisplayName("deve aceitar password com caracteres especiais")
        void shouldAcceptPasswordWithSpecialCharacters() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                TEST_USERNAME,
                "P@ssw0rd!#$%"
            );
            AuthPresenterDTO authResponse = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRATION_MS,
                TEST_USERNAME,
                TEST_EMAIL
            );

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(
                authResponse
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk());

            verify(authService).login(any(LoginRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("consistência de resposta")
    class ResponseConsistency {

        @Test
        @DisplayName(
            "resposta de login deve conter todos os campos obrigatórios"
        )
        void loginResponseShouldContainAllRequiredFields() throws Exception {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO(
                TEST_USERNAME,
                TEST_PASSWORD
            );
            AuthPresenterDTO authResponse = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRATION_MS,
                TEST_USERNAME,
                TEST_EMAIL
            );

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(
                authResponse
            );

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.tokenType").exists());
        }

        @Test
        @DisplayName(
            "resposta de refresh deve conter todos os campos obrigatórios"
        )
        void refreshResponseShouldContainAllRequiredFields() throws Exception {
            // Arrange
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(
                REFRESH_TOKEN
            );
            AuthPresenterDTO authResponse = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRATION_MS,
                TEST_USERNAME,
                TEST_EMAIL
            );

            when(
                authService.refreshToken(any(RefreshTokenRequestDTO.class))
            ).thenReturn(authResponse);

            // Act & Assert
            mockMvc
                .perform(
                    post("/v1/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(refreshRequest)
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.tokenType").exists());
        }
    }
}
