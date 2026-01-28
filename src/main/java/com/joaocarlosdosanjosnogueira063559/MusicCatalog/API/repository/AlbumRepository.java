package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    @Query("SELECT a FROM Album a JOIN a.artists ar WHERE LOWER(ar.name) LIKE LOWER(CONCAT('%', :artistName, '%'))")
    Page<Album> findByArtistName(@Param("artistName") String artistName, Pageable pageable);
}