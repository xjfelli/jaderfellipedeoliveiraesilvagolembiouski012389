package com.gerenciadorartistas.backend.features.artist.repository;

import com.gerenciadorartistas.backend.features.artist.entity.Artist;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);

    List<Artist> findByNameContainingIgnoreCase(String name, Sort sort);

    List<Artist> findByMusicGenreIgnoreCase(String musicGenre);

    List<Artist> findByCountryOfOriginIgnoreCase(String countryOfOrigin);

    @Query(
        "SELECT DISTINCT a FROM Artist a LEFT JOIN FETCH a.albums WHERE a.id = :id"
    )
    Artist findByIdWithAlbums(@Param("id") Long id);

    @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN FETCH a.albums")
    List<Artist> findAllWithAlbums();
}
