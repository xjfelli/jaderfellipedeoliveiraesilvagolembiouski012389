package com.gerenciadorartistas.backend.features.auth.service;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity testUser;

    private static final Long USER_ID = 1L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setFullname("Test User");
        testUser.setRole(UserRole.ROLE_USER);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("deve carregar usuário quando username existe")
        void shouldLoadUserWhenUsernameExists() {
            // Arrange
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(result.getPassword()).isEqualTo(TEST_PASSWORD);
            assertThat(result.isEnabled()).isTrue();
            assertThat(result.isAccountNonExpired()).isTrue();
            assertThat(result.isAccountNonLocked()).isTrue();
            assertThat(result.isCredentialsNonExpired()).isTrue();

            verify(userRepository).findByUsername(TEST_USERNAME);
        }

        @Test
        @DisplayName("deve retornar UserEntity como UserDetails")
        void shouldReturnUserEntityAsUserDetails() {
            // Arrange
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

            // Assert
            assertThat(result).isInstanceOf(UserEntity.class);
            UserEntity userEntity = (UserEntity) result;
            assertThat(userEntity.getId()).isEqualTo(USER_ID);
            assertThat(userEntity.getEmail()).isEqualTo(TEST_EMAIL);
            assertThat(userEntity.getRole()).isEqualTo(UserRole.ROLE_USER);
        }

        @Test
        @DisplayName("deve lançar UsernameNotFoundException quando usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            String nonExistentUsername = "nonexistent";
            when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(nonExistentUsername))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado: " + nonExistentUsername);

            verify(userRepository).findByUsername(nonExistentUsername);
        }

        @Test
        @DisplayName("deve retornar authorities corretas para usuário comum")
        void shouldReturnCorrectAuthoritiesForRegularUser() {
            // Arrange
            testUser.setRole(UserRole.ROLE_USER);
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

            // Assert
            assertThat(result.getAuthorities()).hasSize(1);
            assertThat(result.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("deve retornar authorities corretas para admin")
        void shouldReturnCorrectAuthoritiesForAdmin() {
            // Arrange
            testUser.setRole(UserRole.ROLE_ADMIN);
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

            // Assert
            assertThat(result.getAuthorities()).hasSize(1);
            assertThat(result.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("deve retornar usuário inativo como desabilitado")
        void shouldReturnInactiveUserAsDisabled() {
            // Arrange
            testUser.setIsActive(false);
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

            // Assert
            assertThat(result.isEnabled()).isFalse();
        }
    }

    @Nested
    @DisplayName("loadUserById")
    class LoadUserById {

        @Test
        @DisplayName("deve carregar usuário quando id existe")
        void shouldLoadUserWhenIdExists() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserById(USER_ID);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(result.getPassword()).isEqualTo(TEST_PASSWORD);

            verify(userRepository).findById(USER_ID);
        }

        @Test
        @DisplayName("deve retornar UserEntity como UserDetails por id")
        void shouldReturnUserEntityAsUserDetailsById() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserById(USER_ID);

            // Assert
            assertThat(result).isInstanceOf(UserEntity.class);
            UserEntity userEntity = (UserEntity) result;
            assertThat(userEntity.getId()).isEqualTo(USER_ID);
            assertThat(userEntity.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(userEntity.getEmail()).isEqualTo(TEST_EMAIL);
        }

        @Test
        @DisplayName("deve lançar UsernameNotFoundException quando id não existe")
        void shouldThrowExceptionWhenIdNotFound() {
            // Arrange
            Long nonExistentId = 999L;
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> customUserDetailsService.loadUserById(nonExistentId))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado com id: " + nonExistentId);

            verify(userRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("deve retornar authorities corretas ao carregar por id")
        void shouldReturnCorrectAuthoritiesWhenLoadingById() {
            // Arrange
            testUser.setRole(UserRole.ROLE_ADMIN);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails result = customUserDetailsService.loadUserById(USER_ID);

            // Assert
            assertThat(result.getAuthorities()).hasSize(1);
            assertThat(result.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("consistência entre métodos")
    class MethodsConsistency {

        @Test
        @DisplayName("loadUserByUsername e loadUserById devem retornar o mesmo usuário")
        void loadByUsernameAndByIdShouldReturnSameUser() {
            // Arrange
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act
            UserDetails resultByUsername = customUserDetailsService.loadUserByUsername(TEST_USERNAME);
            UserDetails resultById = customUserDetailsService.loadUserById(USER_ID);

            // Assert
            assertThat(resultByUsername).isEqualTo(resultById);
        }
    }
}
