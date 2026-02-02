package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.AlbumResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.ArtistResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Album;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.AlbumRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository, MinioService minioService,SimpMessagingTemplate messagingTemplate ) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.minioService = minioService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public AlbumResponseDTO create(String title, List<Long> artistIds, MultipartFile file) {

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        boolean isValidImage = (contentType != null && contentType.startsWith("image/")) ||
                (fileName != null && fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|webp|gif)$"));

        if (!isValidImage) {
            throw new IllegalArgumentException("Arquivo inválido. Formatos aceitos: JPG, PNG, WEBP ou GIF.");
        }

        List<Artist> artists = artistRepository.findAllById(artistIds);
        if (artists.size() != artistIds.size()) {
            throw new RuntimeException("Um ou mais IDs de artistas fornecidos não existem");
        }

        String coverId;
        try {
            coverId = minioService.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException("Falha no upload da imagem para o storage", e);
        }

        Album album = new Album();
        album.setTitle(title.trim());
        album.setCoverImageId(coverId);
        album.setArtists(new HashSet<>(artists));

        Album savedAlbum = albumRepository.save(album);
        AlbumResponseDTO response = toDto(savedAlbum);

        try {
            messagingTemplate.convertAndSend("/topic/new-album", response);
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação WebSocket: " + e.getMessage());
        }

        return response;
    }

    @Transactional
    public AlbumResponseDTO update(Long id, String title, List<Long> artistIds, MultipartFile file) {
        return albumRepository.findById(id)
                .map(album -> {
                    if (title != null && !title.isBlank()) {
                        album.setTitle(title.trim());
                    }

                    if (artistIds != null && !artistIds.isEmpty()) {
                        List<Artist> artists = artistRepository.findAllById(artistIds);
                        if (artists.size() != artistIds.size()) {
                            throw new RuntimeException("Lista de artistas contém IDs inválidos");
                        }
                        album.setArtists(new HashSet<>(artists));
                    }

                    if (file != null && !file.isEmpty()) {
                        try {
                            String newCoverId = minioService.uploadFile(file);
                            album.setCoverImageId(newCoverId);
                        } catch (Exception e) {
                            throw new RuntimeException("Falha ao atualizar a imagem no storage", e);
                        }
                    }

                    return albumRepository.save(album);
                })
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Álbum com ID " + id + " não encontrado"));
    }

    @Transactional(readOnly = true)
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
        String coverUrl = null;
        try {
            if (album.getCoverImageId() != null) {
                coverUrl = minioService.generateFileUrl(album.getCoverImageId());
            }
        } catch (Exception e) {
            coverUrl = null;
        }

        Set<ArtistResponseDTO> artistDtos = album.getArtists().stream()
                .map(artist -> new ArtistResponseDTO(artist.getId(), artist.getName()))
                .collect(Collectors.toSet());

        return new AlbumResponseDTO(
                album.getId(),
                album.getTitle(),
                coverUrl,
                artistDtos
        );
    }
}