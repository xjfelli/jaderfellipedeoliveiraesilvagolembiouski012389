package com.gerenciadorartistas.backend.features.auth.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RefreshTokenRequestDTO")
class RefreshTokenRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("construtor")
    class Constructor {

        @Test
        @DisplayName("deve criar instância com construtor padrão")
        void shouldCreateInstanceWithDefaultConstructor() {
            // Act
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getRefreshToken()).isNull();
        }

        @Test
        @DisplayName("deve criar instância com todos os argumentos")
        void shouldCreateInstanceWithAllArgsConstructor() {
            // Arrange
            String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature";

            // Act
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(refreshToken);

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getRefreshToken()).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("getters e setters")
    class GettersAndSetters {

        @Test
        @DisplayName("deve definir e obter refreshToken")
        void shouldSetAndGetRefreshToken() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            String refreshToken = "my.refresh.token";

            // Act
            dto.setRefreshToken(refreshToken);

            // Assert
            assertThat(dto.getRefreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("deve permitir atualizar refreshToken")
        void shouldAllowUpdatingRefreshToken() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("old.token");

            // Act
            dto.setRefreshToken("new.token");

            // Assert
            assertThat(dto.getRefreshToken()).isEqualTo("new.token");
        }
    }

    @Nested
    @DisplayName("validações")
    class Validations {

        @Test
        @DisplayName("não deve ter violações quando refreshToken é válido")
        void shouldHaveNoViolationsWhenRefreshTokenIsValid() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("valid.refresh.token");

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve ter violação quando refreshToken é null")
        void shouldHaveViolationWhenRefreshTokenIsNull() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(null);

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Refresh token é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando refreshToken está vazio")
        void shouldHaveViolationWhenRefreshTokenIsEmpty() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("");

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Refresh token é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando refreshToken contém apenas espaços")
        void shouldHaveViolationWhenRefreshTokenIsBlank() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("   ");

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Refresh token é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação com campo correto")
        void shouldHaveViolationWithCorrectField() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(null);

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("refreshToken");
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToItself() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("refresh.token");

            // Assert
            assertThat(dto).isEqualTo(dto);
        }

        @Test
        @DisplayName("deve ser igual a outro DTO com mesmo valor")
        void shouldBeEqualToAnotherDTOWithSameValue() {
            // Arrange
            RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO("refresh.token");
            RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("refresh.token");

            // Assert
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com valor diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentValue() {
            // Arrange
            RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO("refresh.token.1");
            RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("refresh.token.2");

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("refresh.token");

            // Assert
            assertThat(dto).isNotEqualTo(null);
        }

        @Test
        @DisplayName("não deve ser igual a objeto de outro tipo")
        void shouldNotBeEqualToObjectOfDifferentType() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("refresh.token");

            // Assert
            assertThat(dto).isNotEqualTo("refresh.token");
        }

        @Test
        @DisplayName("DTOs com null devem ser iguais")
        void dtosWithNullShouldBeEqual() {
            // Arrange
            RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO();
            RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO();

            // Assert
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToString {

        @Test
        @DisplayName("deve retornar string contendo o campo")
        void shouldReturnStringContainingField() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("my.refresh.token");

            // Act
            String result = dto.toString();

            // Assert
            assertThat(result).contains("my.refresh.token");
            assertThat(result).contains("RefreshTokenRequestDTO");
        }

        @Test
        @DisplayName("deve retornar string quando refreshToken é null")
        void shouldReturnStringWhenRefreshTokenIsNull() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();

            // Act
            String result = dto.toString();

            // Assert
            assertThat(result).contains("RefreshTokenRequestDTO");
            assertThat(result).contains("null");
        }
    }

    @Nested
    @DisplayName("cenários de uso")
    class UsageScenarios {

        @Test
        @DisplayName("deve aceitar token JWT válido")
        void shouldAcceptValidJwtToken() {
            // Arrange
            String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(jwtToken);

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
            assertThat(dto.getRefreshToken()).isEqualTo(jwtToken);
        }

        @Test
        @DisplayName("deve aceitar token com caracteres especiais")
        void shouldAcceptTokenWithSpecialCharacters() {
            // Arrange
            String token = "token-with_special.characters+123/ABC==";
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(token);

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve aceitar token muito longo")
        void shouldAcceptVeryLongToken() {
            // Arrange
            String longToken = "a".repeat(1000);
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(longToken);

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
            assertThat(dto.getRefreshToken()).hasSize(1000);
        }

        @Test
        @DisplayName("deve aceitar token com um único caractere")
        void shouldAcceptSingleCharacterToken() {
            // Arrange
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("a");

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve manter espaços no início e fim do token")
        void shouldPreserveLeadingAndTrailingSpaces() {
            // Arrange - Token com conteúdo válido mas com espaços
            // NotBlank considera " token " como válido porque tem conteúdo não-branco
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(" token ");

            // Act
            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
            assertThat(dto.getRefreshToken()).isEqualTo(" token ");
        }
    }
}
