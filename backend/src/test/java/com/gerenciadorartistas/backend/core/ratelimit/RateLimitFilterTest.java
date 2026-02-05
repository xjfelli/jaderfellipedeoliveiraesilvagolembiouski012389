package com.gerenciadorartistas.backend.core.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter rateLimitFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowRequestsWhenUnderLimit() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken("testuser", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest();
            MockHttpServletResponse res = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);
            
            rateLimitFilter.doFilterInternal(req, res, chain);
            assertEquals(HttpStatus.OK.value(), res.getStatus());
            verify(chain, times(1)).doFilter(req, res);
        }
    }

    @Test
    void shouldAllowRequestsForUnauthenticatedUsers() throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(null);

        rateLimitFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain, times(1)).doFilter(request, response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void shouldCreateBucketForAuthenticatedUser() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken("newuser", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        
        rateLimitFilter.doFilterInternal(req, res, chain);
        
        assertEquals(HttpStatus.OK.value(), res.getStatus());
        verify(chain, times(1)).doFilter(req, res);
    }
}
