package com.gerenciadorartistas.backend.features.album.repository;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByTitleContainingIgnoreCase(String title);

    List<Album> findByReleaseYear(Integer releaseYear);

    List<Album> findByRecordLabelIgnoreCase(String recordLabel);

    @Query(
        "SELECT DISTINCT a FROM Album a LEFT JOIN FETCH a.artists WHERE a.id = :id"
    )
    Album findByIdWithArtists(@Param("id") Long id);

    @Query("SELECT DISTINCT a FROM Album a LEFT JOIN FETCH a.artists")
    List<Album> findAllWithArtists();

    @Query(
        "SELECT DISTINCT alb FROM Album alb JOIN alb.artists art WHERE art.id = :artistId"
    )
    List<Album> findByArtistId(@Param("artistId") Long artistId);

    @Query(
        "SELECT DISTINCT alb FROM Album alb JOIN alb.artists art WHERE LOWER(art.name) LIKE LOWER(CONCAT('%', :artistName, '%'))"
    )
    List<Album> findByArtistNameContaining(
        @Param("artistName") String artistName
    );
}
