package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.AlbumResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.ArtistResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Album;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.AlbumRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final MinioService minioService;

    public AlbumService(AlbumRepository albumRepository,
                        ArtistRepository artistRepository,
                        MinioService minioService) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.minioService = minioService;
    }

    @Transactional
    public AlbumResponseDTO create(String title, List<Long> artistIds, MultipartFile file) {
        String coverId;
        try {
            coverId = minioService.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem", e);
        }

        List<Artist> artists = artistRepository.findAllById(artistIds);
        if (artists.isEmpty()) {
            throw new RuntimeException("Nenhum artista encontrado com os IDs fornecidos");
        }

        Album album = new Album();
        album.setTitle(title);
        album.setCoverImageId(coverId);
        album.setArtists(new HashSet<>(artists));

        Album savedAlbum = albumRepository.save(album);

        return toDto(savedAlbum);
    }

    @Transactional
    public AlbumResponseDTO update(Long id, String title, List<Long> artistIds) {
        return albumRepository.findById(id)
                .map(album -> {
                    if (title != null && !title.isBlank()) {
                        album.setTitle(title);
                    }

                    if (artistIds != null && !artistIds.isEmpty()) {
                        List<Artist> artists = artistRepository.findAllById(artistIds);
                        if (artists.isEmpty()) {
                            throw new RuntimeException("Artistas não encontrados");
                        }
                        album.setArtists(new HashSet<>(artists));
                    }

                    return albumRepository.save(album);
                })
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));
    }

    public Page<AlbumResponseDTO> findAll(String artistName, Pageable pageable) {
        Page<Album> page;

        if (artistName != null && !artistName.isBlank()) {
            page = albumRepository.findByArtistName(artistName, pageable);
        } else {
            page = albumRepository.findAll(pageable);
        }

        return page.map(this::toDto);
    }

    private AlbumResponseDTO toDto(Album album) {
        String presignedUrl = null;
        try {
            if (album.getCoverImageId() != null) {
                presignedUrl = minioService.generateFileUrl(album.getCoverImageId());
            }
        } catch (Exception e) {
            presignedUrl = null;
        }

        Set<ArtistResponseDTO> artistDtos = album.getArtists().stream()
                .map(artist -> new ArtistResponseDTO(artist.getId(), artist.getName()))
                .collect(Collectors.toSet());
        return new AlbumResponseDTO(
                album.getId(),
                album.getTitle(),
                presignedUrl,
                artistDtos
        );
    }
}