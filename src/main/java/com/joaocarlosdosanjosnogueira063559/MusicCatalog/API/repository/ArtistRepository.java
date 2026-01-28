package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {


}