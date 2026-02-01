package com.gerenciadorartistas.backend.features.artist.entity;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "artist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "music_genre")
    private String musicGenre;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "storage_id")
    private String storageId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "artist_album",
        joinColumns = @JoinColumn(name = "artist_id"),
        inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private Set<Album> albums = new HashSet<>();

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(
        String name,
        String genre,
        String bio,
        String country,
        String photoUrl
    ) {
        this.name = name;
        this.musicGenre = genre;
        this.biography = bio;
        this.countryOfOrigin = country;
        this.photoUrl = photoUrl;
    }

    public void addAlbum(Album album) {
        albums.add(album);
        album.getArtists().add(this);
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
        album.getArtists().remove(this);
    }
}
