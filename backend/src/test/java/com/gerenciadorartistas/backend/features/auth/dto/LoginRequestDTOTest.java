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

@DisplayName("LoginRequestDTO")
class LoginRequestDTOTest {

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
            LoginRequestDTO dto = new LoginRequestDTO();

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getUsername()).isNull();
            assertThat(dto.getPassword()).isNull();
        }

        @Test
        @DisplayName("deve criar instância com todos os argumentos")
        void shouldCreateInstanceWithAllArgsConstructor() {
            // Arrange
            String username = "testuser";
            String password = "password123";

            // Act
            LoginRequestDTO dto = new LoginRequestDTO(username, password);

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getUsername()).isEqualTo(username);
            assertThat(dto.getPassword()).isEqualTo(password);
        }
    }

    @Nested
    @DisplayName("getters e setters")
    class GettersAndSetters {

        @Test
        @DisplayName("deve definir e obter username")
        void shouldSetAndGetUsername() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO();
            String username = "myuser";

            // Act
            dto.setUsername(username);

            // Assert
            assertThat(dto.getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("deve definir e obter password")
        void shouldSetAndGetPassword() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO();
            String password = "mypassword";

            // Act
            dto.setPassword(password);

            // Assert
            assertThat(dto.getPassword()).isEqualTo(password);
        }
    }

    @Nested
    @DisplayName("validações")
    class Validations {

        @Test
        @DisplayName("não deve ter violações quando todos os campos são válidos")
        void shouldHaveNoViolationsWhenAllFieldsAreValid() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "password123");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve ter violação quando username é null")
        void shouldHaveViolationWhenUsernameIsNull() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO(null, "password123");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Username é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando username está vazio")
        void shouldHaveViolationWhenUsernameIsEmpty() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("", "password123");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Username é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando username contém apenas espaços")
        void shouldHaveViolationWhenUsernameIsBlank() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("   ", "password123");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Username é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando password é null")
        void shouldHaveViolationWhenPasswordIsNull() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", null);

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Password é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando password está vazio")
        void shouldHaveViolationWhenPasswordIsEmpty() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Password é obrigatório");
        }

        @Test
        @DisplayName("deve ter violação quando password contém apenas espaços")
        void shouldHaveViolationWhenPasswordIsBlank() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "   ");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Password é obrigatório");
        }

        @Test
        @DisplayName("deve ter duas violações quando username e password são null")
        void shouldHaveTwoViolationsWhenBothFieldsAreNull() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO(null, null);

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(2);
        }

        @Test
        @DisplayName("deve ter duas violações quando username e password estão vazios")
        void shouldHaveTwoViolationsWhenBothFieldsAreEmpty() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("", "");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(2);
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToItself() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "password123");

            // Assert
            assertThat(dto).isEqualTo(dto);
        }

        @Test
        @DisplayName("deve ser igual a outro DTO com mesmos valores")
        void shouldBeEqualToAnotherDTOWithSameValues() {
            // Arrange
            LoginRequestDTO dto1 = new LoginRequestDTO("testuser", "password123");
            LoginRequestDTO dto2 = new LoginRequestDTO("testuser", "password123");

            // Assert
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com username diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentUsername() {
            // Arrange
            LoginRequestDTO dto1 = new LoginRequestDTO("testuser1", "password123");
            LoginRequestDTO dto2 = new LoginRequestDTO("testuser2", "password123");

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a outro DTO com password diferente")
        void shouldNotBeEqualToAnotherDTOWithDifferentPassword() {
            // Arrange
            LoginRequestDTO dto1 = new LoginRequestDTO("testuser", "password123");
            LoginRequestDTO dto2 = new LoginRequestDTO("testuser", "password456");

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "password123");

            // Assert
            assertThat(dto).isNotEqualTo(null);
        }

        @Test
        @DisplayName("não deve ser igual a objeto de outro tipo")
        void shouldNotBeEqualToObjectOfDifferentType() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "password123");

            // Assert
            assertThat(dto).isNotEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToString {

        @Test
        @DisplayName("deve retornar string contendo os campos")
        void shouldReturnStringContainingFields() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "password123");

            // Act
            String result = dto.toString();

            // Assert
            assertThat(result).contains("testuser");
            assertThat(result).contains("password123");
            assertThat(result).contains("LoginRequestDTO");
        }
    }

    @Nested
    @DisplayName("cenários de uso")
    class UsageScenarios {

        @Test
        @DisplayName("deve aceitar username com email")
        void shouldAcceptUsernameWithEmail() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("user@domain.com", "password123");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
            assertThat(dto.getUsername()).isEqualTo("user@domain.com");
        }

        @Test
        @DisplayName("deve aceitar password com caracteres especiais")
        void shouldAcceptPasswordWithSpecialCharacters() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("testuser", "P@ssw0rd!#$%^&*()");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
            assertThat(dto.getPassword()).isEqualTo("P@ssw0rd!#$%^&*()");
        }

        @Test
        @DisplayName("deve aceitar username com números")
        void shouldAcceptUsernameWithNumbers() {
            // Arrange
            LoginRequestDTO dto = new LoginRequestDTO("user123", "password123");

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("deve aceitar password longa")
        void shouldAcceptLongPassword() {
            // Arrange
            String longPassword = "a".repeat(100);
            LoginRequestDTO dto = new LoginRequestDTO("testuser", longPassword);

            // Act
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
            assertThat(dto.getPassword()).hasSize(100);
        }
    }
}
