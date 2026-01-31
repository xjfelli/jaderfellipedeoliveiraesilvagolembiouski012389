package com.gerenciadorartistas.backend.features.artist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArtistDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String name;

    @Size(
        max = 100,
        message = "Gênero musical deve ter no máximo 100 caracteres"
    )
    private String musicalGenre;

    private String biography;

    @Size(
        max = 100,
        message = "País de origem deve ter no máximo 100 caracteres"
    )
    private String countryOfOrigin;

    @Size(max = 500, message = "URL da foto deve ter no máximo 500 caracteres")
    private String photoUrl;

    public ArtistDTO() {}

    public ArtistDTO(
        String name,
        String musicalGenre,
        String biography,
        String countryOfOrigin,
        String photoUrl
    ) {
        this.name = name;
        this.musicalGenre = musicalGenre;
        this.biography = biography;
        this.countryOfOrigin = countryOfOrigin;
        this.photoUrl = photoUrl;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMusicalGenre() {
        return musicalGenre;
    }

    public void setMusicalGenre(String musicalGenre) {
        this.musicalGenre = musicalGenre;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
