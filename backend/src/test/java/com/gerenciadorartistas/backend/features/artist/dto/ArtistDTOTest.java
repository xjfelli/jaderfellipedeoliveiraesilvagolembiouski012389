package com.gerenciadorartistas.backend.features.artist.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArtistDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenAllFieldsValid_ShouldPassValidation() {
        ArtistDTO dto = new ArtistDTO();
        dto.setName("Valid Artist");
        dto.setMusicalGenre("Rock");
        dto.setBiography("A great artist");
        dto.setCountryOfOrigin("USA");
        dto.setPhotoUrl("http://example.com/photo.jpg");

        Set<ConstraintViolation<ArtistDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsBlank_ShouldFailValidation() {
        ArtistDTO dto = new ArtistDTO();
        dto.setName("");

        Set<ConstraintViolation<ArtistDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Nome é obrigatório")));
    }

    @Test
    void whenNameExceedsMaxLength_ShouldFailValidation() {
        ArtistDTO dto = new ArtistDTO();
        dto.setName("a".repeat(256));

        Set<ConstraintViolation<ArtistDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        ArtistDTO dto = new ArtistDTO();
        dto.setName("Artist");
        dto.setMusicalGenre("Rock");
        dto.setBiography("Bio");
        dto.setCountryOfOrigin("Brazil");
        dto.setPhotoUrl("photo.jpg");

        assertEquals("Artist", dto.getName());
        assertEquals("Rock", dto.getMusicalGenre());
        assertEquals("Bio", dto.getBiography());
        assertEquals("Brazil", dto.getCountryOfOrigin());
        assertEquals("photo.jpg", dto.getPhotoUrl());
    }
}
