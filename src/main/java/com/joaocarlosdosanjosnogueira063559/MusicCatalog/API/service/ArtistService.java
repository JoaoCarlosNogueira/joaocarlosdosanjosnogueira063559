package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.ArtistResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional
    public ArtistResponseDTO create(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        Artist savedArtist = artistRepository.save(artist);
        return toDto(savedArtist);
    }

    @Transactional
    public ArtistResponseDTO update(Long id, String name) {
        return artistRepository.findById(id)
                .map(artist -> {
                    artist.setName(name);
                    return artistRepository.save(artist);
                })
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));
    }

    public Page<ArtistResponseDTO> findAll(Pageable pageable) {
        return artistRepository.findAll(pageable).map(this::toDto);
    }

    private ArtistResponseDTO toDto(Artist artist) {
        return new ArtistResponseDTO(artist.getId(), artist.getName());
    }
}