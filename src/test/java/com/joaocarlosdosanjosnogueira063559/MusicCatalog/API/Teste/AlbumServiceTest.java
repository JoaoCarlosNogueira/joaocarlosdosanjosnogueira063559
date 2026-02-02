package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.Teste;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.AlbumResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Album;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.AlbumRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.AlbumService;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.MinioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock private AlbumRepository albumRepository;
    @Mock private ArtistRepository artistRepository;
    @Mock private MinioService minioService;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks private AlbumService albumService;

    @Test
    @DisplayName("Deve criar um álbum com sucesso e fazer upload da capa")
    void create_ShouldSaveAlbum_WhenDataIsValid() throws Exception {
        // Arrange
        String title = "Meteora";
        List<Long> artistIds = List.of(1L);
        Artist artist = new Artist(1L, "Linkin Park", new HashSet<>());
        MockMultipartFile file = new MockMultipartFile("file", "capa.png", "image/png", "bytes".getBytes());

        when(artistRepository.findAllById(artistIds)).thenReturn(List.of(artist));
        when(minioService.uploadFile(any())).thenReturn("minio-uuid-123");
        when(albumRepository.save(any(Album.class))).thenAnswer(i -> {
            Album a = i.getArgument(0);
            a.setId(100L);
            return a;
        });

        AlbumResponseDTO result = albumService.create(title, artistIds, file);

        assertNotNull(result);
        assertEquals("Meteora", result.getTitle());
        verify(minioService, times(1)).uploadFile(file);
        verify(albumRepository, times(1)).save(any());
        verify(messagingTemplate).convertAndSend(eq("/topic/new-album"), any(AlbumResponseDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar título e imagem do álbum com sucesso")
    void update_ShouldUpdateDataAndImage_WhenAlbumExists() throws Exception {
        Long id = 100L;
        Album existingAlbum = new Album(new HashSet<>(), null, "Old Title", id);
        MockMultipartFile newFile = new MockMultipartFile("file", "nova.jpg", "image/jpeg", "content".getBytes());

        when(albumRepository.findById(id)).thenReturn(Optional.of(existingAlbum));
        when(minioService.uploadFile(newFile)).thenReturn("new-image-id");
        when(albumRepository.save(any(Album.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        AlbumResponseDTO result = albumService.update(id, "New Title", null, newFile);

        // Assert
        assertEquals("New Title", result.getTitle());
        verify(minioService).uploadFile(newFile);
        verify(albumRepository).save(existingAlbum);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar álbum com arquivo que não é imagem")
    void create_ShouldThrowException_WhenFileIsNotImage() {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "pdf-data".getBytes());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                albumService.create("Title", List.of(1L), invalidFile)
        );
    }
}