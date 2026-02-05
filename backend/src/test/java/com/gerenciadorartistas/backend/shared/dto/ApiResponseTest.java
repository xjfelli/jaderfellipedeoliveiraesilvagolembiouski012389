package com.gerenciadorartistas.backend.shared.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_ShouldCreateSuccessResponse() {
        ApiResponse<String> response = ApiResponse.success("data");
        
        assertTrue(response.isSuccess());
        assertEquals("data", response.getData());
        assertNull(response.getError());
    }

    @Test
    void error_ShouldCreateErrorResponse() {
        ApiResponse<String> response = ApiResponse.error("ERR_CODE", "error message");
        
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("ERR_CODE", response.getError().getCode());
        assertEquals("error message", response.getError().getMessage());
    }
}
