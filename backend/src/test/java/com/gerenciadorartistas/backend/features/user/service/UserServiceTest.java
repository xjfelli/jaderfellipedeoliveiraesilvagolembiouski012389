package com.gerenciadorartistas.backend.features.user.service;

import com.gerenciadorartistas.backend.features.user.dto.UserCreateDTO;
import com.gerenciadorartistas.backend.features.user.dto.UserPresenterDTO;
import com.gerenciadorartistas.backend.features.user.entity.UserEntity;
import com.gerenciadorartistas.backend.features.user.entity.UserRole;
import com.gerenciadorartistas.backend.features.user.mapper.UserMapper;
import com.gerenciadorartistas.backend.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity user;
    private UserCreateDTO userCreateDTO;
    private UserPresenterDTO userPresenterDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFullname("Test User");
        user.setPassword("encoded");
        user.setRole(UserRole.ROLE_USER);

        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setFullname("Test User");
        userCreateDTO.setPassword("password123");

        userPresenterDTO = new UserPresenterDTO(1L, "testuser", "test@example.com", "Test User");
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toDto(any())).thenReturn(userPresenterDTO);

        List<UserPresenterDTO> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any())).thenReturn(userPresenterDTO);

        UserPresenterDTO result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.username());
    }

    @Test
    void create_WithValidData_ShouldCreateUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userPresenterDTO);

        UserPresenterDTO result = userService.create(userCreateDTO);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void delete_WhenExists_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }
}
