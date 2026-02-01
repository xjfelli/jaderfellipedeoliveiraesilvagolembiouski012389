package com.gerenciadorartistas.backend.features.artist.dto;

import com.gerenciadorartistas.backend.features.artist.entity.Artist;

public class ArtistMinimalDTO {

    private Long id;
    private String nome;
    private String generoMusical;
    private String fotoUrl;

    public ArtistMinimalDTO() {}

    public ArtistMinimalDTO(Artist artista) {
        this.id = artista.getId();
        this.nome = artista.getName();
        this.generoMusical = artista.getMusicGenre();
        this.fotoUrl = artista.getPhotoUrl();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public void setGeneroMusical(String generoMusical) {
        this.generoMusical = generoMusical;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
