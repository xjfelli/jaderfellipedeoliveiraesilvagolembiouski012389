package com.gerenciadorartistas.backend.features.user.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void settersAndGetters_ShouldWork() {
        UserEntity user = new UserEntity();
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setRole(UserRole.ROLE_USER);
        
        assertEquals("test", user.getUsername());
        assertEquals("test@test.com", user.getEmail());
        assertEquals(UserRole.ROLE_USER, user.getRole());
    }
}
