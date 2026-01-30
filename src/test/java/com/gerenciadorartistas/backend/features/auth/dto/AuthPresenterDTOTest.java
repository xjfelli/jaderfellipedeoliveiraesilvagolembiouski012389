package com.gerenciadorartistas.backend.features.auth.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthPresenterDTO")
class AuthPresenterDTOTest {

    private static final String ACCESS_TOKEN = "access.token.jwt";
    private static final String REFRESH_TOKEN = "refresh.token.jwt";
    private static final Long EXPIRES_IN = 3600000L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@email.com";

    @Nested
    @DisplayName("construtor")
    class Constructor {

        @Test
        @DisplayName("deve criar instância com construtor padrão")
        void shouldCreateInstanceWithDefaultConstructor() {
            // Act
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getAccessToken()).isNull();
            assertThat(dto.getRefreshToken()).isNull();
            assertThat(dto.getTokenType()).isEqualTo("Bearer");
            assertThat(dto.getExpiresIn()).isNull();
            assertThat(dto.getUsername()).isNull();
            assertThat(dto.getEmail()).isNull();
        }

        @Test
        @DisplayName("deve criar instância com construtor de 5 argumentos")
        void shouldCreateInstanceWithFiveArgsConstructor() {
            // Act
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getAccessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(dto.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(dto.getTokenType()).isEqualTo("Bearer");
            assertThat(dto.getExpiresIn()).isEqualTo(EXPIRES_IN);
            assertThat(dto.getUsername()).isEqualTo(USERNAME);
            assertThat(dto.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("deve criar instância com todos os argumentos")
        void shouldCreateInstanceWithAllArgsConstructor() {
            // Act
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                "CustomToken",
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getAccessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(dto.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(dto.getTokenType()).isEqualTo("CustomToken");
            assertThat(dto.getExpiresIn()).isEqualTo(EXPIRES_IN);
            assertThat(dto.getUsername()).isEqualTo(USERNAME);
            assertThat(dto.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("construtor de 5 argumentos deve definir tokenType como Bearer")
        void fiveArgsConstructorShouldSetTokenTypeAsBearer() {
            // Act
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto.getTokenType()).isEqualTo("Bearer");
        }
    }

    @Nested
    @DisplayName("getters e setters")
    class GettersAndSetters {

        @Test
        @DisplayName("deve definir e obter accessToken")
        void shouldSetAndGetAccessToken() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setAccessToken(ACCESS_TOKEN);

            // Assert
            assertThat(dto.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        }

        @Test
        @DisplayName("deve definir e obter refreshToken")
        void shouldSetAndGetRefreshToken() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setRefreshToken(REFRESH_TOKEN);

            // Assert
            assertThat(dto.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("deve definir e obter tokenType")
        void shouldSetAndGetTokenType() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setTokenType("CustomType");

            // Assert
            assertThat(dto.getTokenType()).isEqualTo("CustomType");
        }

        @Test
        @DisplayName("deve definir e obter expiresIn")
        void shouldSetAndGetExpiresIn() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setExpiresIn(EXPIRES_IN);

            // Assert
            assertThat(dto.getExpiresIn()).isEqualTo(EXPIRES_IN);
        }

        @Test
        @DisplayName("deve definir e obter username")
        void shouldSetAndGetUsername() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setUsername(USERNAME);

            // Assert
            assertThat(dto.getUsername()).isEqualTo(USERNAME);
        }

        @Test
        @DisplayName("deve definir e obter email")
        void shouldSetAndGetEmail() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setEmail(EMAIL);

            // Assert
            assertThat(dto.getEmail()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("valor padrão de tokenType")
    class DefaultTokenType {

        @Test
        @DisplayName("tokenType deve ser Bearer por padrão no construtor padrão")
        void tokenTypeShouldBeBearerByDefaultInDefaultConstructor() {
            // Act
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Assert
            assertThat(dto.getTokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("tokenType deve ser Bearer no construtor de 5 argumentos")
        void tokenTypeShouldBeBearerInFiveArgsConstructor() {
            // Act
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto.getTokenType()).isEqualTo("Bearer");
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToItself() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto).isEqualTo(dto);
        }

        @Test
        @DisplayName("deve ser igual a outro DTO com mesmos valores")
        void shouldBeEqualToAnotherDTOWithSameValues() {
            // Arrange
            AuthPresenterDTO dto1 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );
            AuthPresenterDTO dto2 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com accessToken diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentAccessToken() {
            // Arrange
            AuthPresenterDTO dto1 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );
            AuthPresenterDTO dto2 = new AuthPresenterDTO(
                "different.access.token",
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com refreshToken diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentRefreshToken() {
            // Arrange
            AuthPresenterDTO dto1 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );
            AuthPresenterDTO dto2 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                "different.refresh.token",
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com username diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentUsername() {
            // Arrange
            AuthPresenterDTO dto1 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );
            AuthPresenterDTO dto2 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                "differentuser",
                EMAIL
            );

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com email diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentEmail() {
            // Arrange
            AuthPresenterDTO dto1 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );
            AuthPresenterDTO dto2 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                "different@email.com"
            );

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com expiresIn diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentExpiresIn() {
            // Arrange
            AuthPresenterDTO dto1 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );
            AuthPresenterDTO dto2 = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                7200000L,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto).isNotEqualTo(null);
        }

        @Test
        @DisplayName("não deve ser igual a objeto de outro tipo")
        void shouldNotBeEqualToObjectOfDifferentType() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Assert
            assertThat(dto).isNotEqualTo("string");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToString {

        @Test
        @DisplayName("deve retornar string contendo todos os campos")
        void shouldReturnStringContainingAllFields() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                EMAIL
            );

            // Act
            String result = dto.toString();

            // Assert
            assertThat(result).contains("AuthPresenterDTO");
            assertThat(result).contains(ACCESS_TOKEN);
            assertThat(result).contains(REFRESH_TOKEN);
            assertThat(result).contains("Bearer");
            assertThat(result).contains(EXPIRES_IN.toString());
            assertThat(result).contains(USERNAME);
            assertThat(result).contains(EMAIL);
        }
    }

    @Nested
    @DisplayName("cenários de uso")
    class UsageScenarios {

        @Test
        @DisplayName("deve permitir atualizar todos os campos")
        void shouldAllowUpdatingAllFields() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO();

            // Act
            dto.setAccessToken("new.access.token");
            dto.setRefreshToken("new.refresh.token");
            dto.setTokenType("CustomBearer");
            dto.setExpiresIn(7200000L);
            dto.setUsername("newuser");
            dto.setEmail("new@email.com");

            // Assert
            assertThat(dto.getAccessToken()).isEqualTo("new.access.token");
            assertThat(dto.getRefreshToken()).isEqualTo("new.refresh.token");
            assertThat(dto.getTokenType()).isEqualTo("CustomBearer");
            assertThat(dto.getExpiresIn()).isEqualTo(7200000L);
            assertThat(dto.getUsername()).isEqualTo("newuser");
            assertThat(dto.getEmail()).isEqualTo("new@email.com");
        }

        @Test
        @DisplayName("deve aceitar valores null para campos opcionais")
        void shouldAcceptNullValuesForOptionalFields() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                null,
                null,
                null,
                null,
                null
            );

            // Assert
            assertThat(dto.getAccessToken()).isNull();
            assertThat(dto.getRefreshToken()).isNull();
            assertThat(dto.getExpiresIn()).isNull();
            assertThat(dto.getUsername()).isNull();
            assertThat(dto.getEmail()).isNull();
            // tokenType mantém valor padrão no campo, mas construtor 5-args não o sobrescreve
        }

        @Test
        @DisplayName("deve representar resposta de autenticação completa")
        void shouldRepresentCompleteAuthResponse() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                "eyJhbGciOiJIUzI1NiJ9.access.token",
                "eyJhbGciOiJIUzI1NiJ9.refresh.token",
                3600000L,
                "admin",
                "admin@example.com"
            );

            // Assert
            assertThat(dto.getAccessToken()).startsWith("eyJhbGciOiJIUzI1NiJ9");
            assertThat(dto.getRefreshToken()).startsWith("eyJhbGciOiJIUzI1NiJ9");
            assertThat(dto.getTokenType()).isEqualTo("Bearer");
            assertThat(dto.getExpiresIn()).isEqualTo(3600000L);
            assertThat(dto.getUsername()).isEqualTo("admin");
            assertThat(dto.getEmail()).isEqualTo("admin@example.com");
        }

        @Test
        @DisplayName("expiresIn deve aceitar diferentes valores de tempo")
        void expiresInShouldAcceptDifferentTimeValues() {
            // Arrange & Act
            AuthPresenterDTO dto1Hr = new AuthPresenterDTO(
                ACCESS_TOKEN, REFRESH_TOKEN, 3600000L, USERNAME, EMAIL
            );
            AuthPresenterDTO dto24Hr = new AuthPresenterDTO(
                ACCESS_TOKEN, REFRESH_TOKEN, 86400000L, USERNAME, EMAIL
            );

            // Assert
            assertThat(dto1Hr.getExpiresIn()).isEqualTo(3600000L);
            assertThat(dto24Hr.getExpiresIn()).isEqualTo(86400000L);
        }

        @Test
        @DisplayName("deve aceitar email em formato válido")
        void shouldAcceptValidEmailFormat() {
            // Arrange
            AuthPresenterDTO dto = new AuthPresenterDTO(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN,
                USERNAME,
                "user.name+tag@subdomain.example.com"
            );

            // Assert
            assertThat(dto.getEmail()).isEqualTo("user.name+tag@subdomain.example.com");
        }
    }
}
