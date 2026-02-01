package com.gerenciadorartistas.backend.features.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private static final String JWT_SECRET =
        "minha-chave-secreta-super-segura-para-testes-jwt-256-bits";
    private static final Long JWT_EXPIRATION_MS = 3600000L; // 1 hora
    private static final Long JWT_REFRESH_EXPIRATION_MS = 86400000L; // 24 horas
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(
            jwtTokenProvider,
            "jwtExpirationMs",
            JWT_EXPIRATION_MS
        );
        ReflectionTestUtils.setField(
            jwtTokenProvider,
            "jwtRefreshExpirationMs",
            JWT_REFRESH_EXPIRATION_MS
        );
        jwtTokenProvider.init();
    }

    @Nested
    @DisplayName("generateAccessToken")
    class GenerateAccessToken {

        @Test
        @DisplayName("deve gerar access token a partir de Authentication")
        void shouldGenerateAccessTokenFromAuthentication() {
            // Arrange
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

            // Act
            String token = jwtTokenProvider.generateAccessToken(authentication);

            // Assert
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT tem 3 partes
        }

        @Test
        @DisplayName("deve gerar access token a partir de username")
        void shouldGenerateAccessTokenFromUsername() {
            // Act
            String token = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Assert
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("access token não deve ser refresh token")
        void accessTokenShouldNotBeRefreshToken() {
            // Act
            String token = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Assert
            assertThat(jwtTokenProvider.isRefreshToken(token)).isFalse();
        }
    }

    @Nested
    @DisplayName("generateRefreshToken")
    class GenerateRefreshToken {

        @Test
        @DisplayName("deve gerar refresh token")
        void shouldGenerateRefreshToken() {
            // Act
            String token = jwtTokenProvider.generateRefreshToken(TEST_USERNAME);

            // Assert
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("refresh token deve ser identificado como refresh token")
        void refreshTokenShouldBeIdentifiedAsRefreshToken() {
            // Act
            String token = jwtTokenProvider.generateRefreshToken(TEST_USERNAME);

            // Assert
            assertThat(jwtTokenProvider.isRefreshToken(token)).isTrue();
        }
    }

    @Nested
    @DisplayName("getUsernameFromToken")
    class GetUsernameFromToken {

        @Test
        @DisplayName("deve extrair username do access token")
        void shouldExtractUsernameFromAccessToken() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Act
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Assert
            assertThat(username).isEqualTo(TEST_USERNAME);
        }

        @Test
        @DisplayName("deve extrair username do refresh token")
        void shouldExtractUsernameFromRefreshToken() {
            // Arrange
            String token = jwtTokenProvider.generateRefreshToken(TEST_USERNAME);

            // Act
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Assert
            assertThat(username).isEqualTo(TEST_USERNAME);
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("deve validar access token válido")
        void shouldValidateValidAccessToken() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Act
            boolean isValid = jwtTokenProvider.validateToken(token);

            // Assert
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("deve validar refresh token válido")
        void shouldValidateValidRefreshToken() {
            // Arrange
            String token = jwtTokenProvider.generateRefreshToken(TEST_USERNAME);

            // Act
            boolean isValid = jwtTokenProvider.validateToken(token);

            // Assert
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("deve rejeitar token inválido")
        void shouldRejectInvalidToken() {
            // Act
            boolean isValid = jwtTokenProvider.validateToken(
                "token.invalido.aqui"
            );

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("deve rejeitar token malformado")
        void shouldRejectMalformedToken() {
            // Act
            boolean isValid = jwtTokenProvider.validateToken(
                "token-malformado"
            );

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("deve rejeitar token vazio")
        void shouldRejectEmptyToken() {
            // Act
            boolean isValid = jwtTokenProvider.validateToken("");

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("deve rejeitar token expirado")
        void shouldRejectExpiredToken() {
            // Arrange - Criar provider com expiração muito curta
            JwtTokenProvider expiredProvider = new JwtTokenProvider();
            ReflectionTestUtils.setField(
                expiredProvider,
                "jwtSecret",
                JWT_SECRET
            );
            ReflectionTestUtils.setField(
                expiredProvider,
                "jwtExpirationMs",
                1L
            ); // 1ms
            ReflectionTestUtils.setField(
                expiredProvider,
                "jwtRefreshExpirationMs",
                1L
            );
            expiredProvider.init();

            String token = expiredProvider.generateAccessToken(TEST_USERNAME);

            // Aguardar expiração
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Act
            boolean isValid = expiredProvider.validateToken(token);

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("deve rejeitar token com assinatura inválida")
        void shouldRejectTokenWithInvalidSignature() {
            // Arrange - Criar token com outra chave secreta
            JwtTokenProvider otherProvider = new JwtTokenProvider();
            ReflectionTestUtils.setField(
                otherProvider,
                "jwtSecret",
                "outra-chave-secreta-diferente-para-teste-256-bits"
            );
            ReflectionTestUtils.setField(
                otherProvider,
                "jwtExpirationMs",
                JWT_EXPIRATION_MS
            );
            ReflectionTestUtils.setField(
                otherProvider,
                "jwtRefreshExpirationMs",
                JWT_REFRESH_EXPIRATION_MS
            );
            otherProvider.init();

            String tokenFromOtherProvider = otherProvider.generateAccessToken(
                TEST_USERNAME
            );

            // Act - Validar com provider original
            boolean isValid = jwtTokenProvider.validateToken(
                tokenFromOtherProvider
            );

            // Assert
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("isRefreshToken")
    class IsRefreshToken {

        @Test
        @DisplayName("deve retornar true para refresh token")
        void shouldReturnTrueForRefreshToken() {
            // Arrange
            String token = jwtTokenProvider.generateRefreshToken(TEST_USERNAME);

            // Act
            boolean isRefresh = jwtTokenProvider.isRefreshToken(token);

            // Assert
            assertThat(isRefresh).isTrue();
        }

        @Test
        @DisplayName("deve retornar false para access token")
        void shouldReturnFalseForAccessToken() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Act
            boolean isRefresh = jwtTokenProvider.isRefreshToken(token);

            // Assert
            assertThat(isRefresh).isFalse();
        }

        @Test
        @DisplayName("deve retornar false para token inválido")
        void shouldReturnFalseForInvalidToken() {
            // Act
            boolean isRefresh = jwtTokenProvider.isRefreshToken(
                "token.invalido.aqui"
            );

            // Assert
            assertThat(isRefresh).isFalse();
        }
    }

    @Nested
    @DisplayName("getExpirationMs")
    class GetExpirationMs {

        @Test
        @DisplayName("deve retornar tempo de expiração configurado")
        void shouldReturnConfiguredExpirationTime() {
            // Act
            Long expirationMs = jwtTokenProvider.getExpirationMs();

            // Assert
            assertThat(expirationMs).isEqualTo(JWT_EXPIRATION_MS);
        }
    }

    @Nested
    @DisplayName("tokens diferentes")
    class DifferentTokens {

        @Test
        @DisplayName(
            "deve gerar tokens diferentes para o mesmo usuário em momentos diferentes"
        )
        void shouldGenerateDifferentTokensForSameUserAtDifferentTimes()
            throws InterruptedException {
            // Act
            String token1 = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Aguardar 1 segundo para garantir que o timestamp seja diferente
            Thread.sleep(1001);

            String token2 = jwtTokenProvider.generateAccessToken(TEST_USERNAME);

            // Assert
            assertThat(token1).isNotEqualTo(token2);
        }

        @Test
        @DisplayName("deve gerar tokens diferentes para usuários diferentes")
        void shouldGenerateDifferentTokensForDifferentUsers() {
            // Act
            String token1 = jwtTokenProvider.generateAccessToken("user1");
            String token2 = jwtTokenProvider.generateAccessToken("user2");

            // Assert
            assertThat(token1).isNotEqualTo(token2);
        }

        @Test
        @DisplayName("access e refresh token devem ser diferentes")
        void accessAndRefreshTokenShouldBeDifferent() {
            // Act
            String accessToken = jwtTokenProvider.generateAccessToken(
                TEST_USERNAME
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                TEST_USERNAME
            );

            // Assert
            assertThat(accessToken).isNotEqualTo(refreshToken);
        }
    }
}
