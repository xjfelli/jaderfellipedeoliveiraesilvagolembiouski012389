package com.gerenciadorartistas.backend.features.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternal {

        @Test
        @DisplayName("deve autenticar usuário com token válido")
        void shouldAuthenticateUserWithValidToken() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(VALID_TOKEN)).thenReturn(false);
            when(tokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
            when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userDetails);

            verify(filterChain).doFilter(request, response);
            verify(tokenProvider).validateToken(VALID_TOKEN);
            verify(tokenProvider).isRefreshToken(VALID_TOKEN);
            verify(tokenProvider).getUsernameFromToken(VALID_TOKEN);
            verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
        }

        @Test
        @DisplayName("não deve autenticar quando token é inválido")
        void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {
            // Arrange
            String invalidToken = "invalid.token";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
            when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(filterChain).doFilter(request, response);
            verify(tokenProvider).validateToken(invalidToken);
            verify(tokenProvider, never()).getUsernameFromToken(anyString());
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("não deve autenticar quando Authorization header está ausente")
        void shouldNotAuthenticateWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("não deve autenticar quando Authorization header não começa com Bearer")
        void shouldNotAuthenticateWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Basic " + VALID_TOKEN);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("não deve autenticar quando token é refresh token")
        void shouldNotAuthenticateWhenTokenIsRefreshToken() throws ServletException, IOException {
            // Arrange
            String refreshToken = "refresh.token.jwt";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + refreshToken);
            when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
            when(tokenProvider.isRefreshToken(refreshToken)).thenReturn(true);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(filterChain).doFilter(request, response);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider).isRefreshToken(refreshToken);
            verify(tokenProvider, never()).getUsernameFromToken(anyString());
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("deve continuar filter chain mesmo quando token é inválido")
        void shouldContinueFilterChainEvenWhenTokenIsInvalid() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid");
            when(tokenProvider.validateToken("invalid")).thenReturn(false);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("deve continuar filter chain quando ocorre exceção")
        void shouldContinueFilterChainWhenExceptionOccurs() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(tokenProvider.validateToken(VALID_TOKEN)).thenThrow(new RuntimeException("Token error"));

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("não deve autenticar com Authorization header vazio")
        void shouldNotAuthenticateWithEmptyAuthorizationHeader() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("");

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("não deve autenticar com apenas 'Bearer' sem token")
        void shouldNotAuthenticateWithOnlyBearerWithoutToken() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("extração de JWT")
    class JwtExtraction {

        @Test
        @DisplayName("deve extrair token corretamente do header Authorization")
        void shouldExtractTokenCorrectlyFromAuthorizationHeader() throws ServletException, IOException {
            // Arrange
            String specificToken = "my.specific.token";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + specificToken);
            when(tokenProvider.validateToken(specificToken)).thenReturn(true);
            when(tokenProvider.isRefreshToken(specificToken)).thenReturn(false);
            when(tokenProvider.getUsernameFromToken(specificToken)).thenReturn(TEST_USERNAME);
            when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(tokenProvider).validateToken(specificToken);
            verify(tokenProvider).getUsernameFromToken(specificToken);
        }

        @Test
        @DisplayName("deve ignorar espaços extras após Bearer")
        void shouldHandleTokenWithSpaces() throws ServletException, IOException {
            // Arrange - O filtro extrai a partir da posição 7 (após "Bearer ")
            when(request.getHeader("Authorization")).thenReturn("Bearer  token.with.space");
            when(tokenProvider.validateToken(" token.with.space")).thenReturn(false);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("cenários de segurança")
    class SecurityScenarios {

        @Test
        @DisplayName("não deve sobrescrever autenticação existente quando token é válido")
        void shouldAuthenticateWithValidTokenWhenNoExistingAuthentication() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(VALID_TOKEN)).thenReturn(false);
            when(tokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
            when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userDetails);
        }

        @Test
        @DisplayName("deve limpar contexto antes de cada teste")
        void shouldHaveCleanSecurityContextBeforeTest() {
            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    @Nested
    @DisplayName("tratamento de erros")
    class ErrorHandling {

        @Test
        @DisplayName("deve tratar exceção ao carregar usuário")
        void shouldHandleExceptionWhenLoadingUser() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(VALID_TOKEN)).thenReturn(false);
            when(tokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
            when(userDetailsService.loadUserByUsername(TEST_USERNAME))
                    .thenThrow(new RuntimeException("User not found"));

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("deve tratar exceção ao extrair username do token")
        void shouldHandleExceptionWhenExtractingUsername() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
            when(tokenProvider.isRefreshToken(VALID_TOKEN)).thenReturn(false);
            when(tokenProvider.getUsernameFromToken(VALID_TOKEN))
                    .thenThrow(new RuntimeException("Token parsing error"));

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }
}
