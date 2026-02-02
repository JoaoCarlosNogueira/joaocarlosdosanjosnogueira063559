package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.Teste;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.ArtistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.ArtistResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    @DisplayName("Deve cadastrar um novo artista com sucesso")
    void create_ShouldSaveAndReturnArtistResponseDTO() {
        String artistName = "Linkin Park";
        Artist savedArtist = new Artist();
        savedArtist.setId(10L);
        savedArtist.setName(artistName);

        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        ArtistResponseDTO result = artistService.create(artistName);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(artistName, result.getName());

        verify(artistRepository).save(argThat(artist ->
                artist.getName().equals(artistName) && artist.getId() == null
        ));
    }

    @Test
    @DisplayName("Deve atualizar o nome do artista com sucesso")
    void update_ShouldUpdateArtistName_WhenArtistExists() {
        Long id = 1L;
        String newName = "Coldplay Updated";
        Artist existingArtist = new Artist(id, "Coldplay", new HashSet<>());

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArtistResponseDTO result = artistService.update(id, newName);

        assertNotNull(result);
        assertEquals(newName, result.getName());
        verify(artistRepository, times(1)).save(existingArtist);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o artista não for encontrado")
    void update_ShouldThrowException_WhenArtistDoesNotExist() {
        Long id = 99L;
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.update(id, "Any Name");
        });

        assertEquals("Artista não encontrado", exception.getMessage());
        verify(artistRepository, never()).save(any());
    }
}