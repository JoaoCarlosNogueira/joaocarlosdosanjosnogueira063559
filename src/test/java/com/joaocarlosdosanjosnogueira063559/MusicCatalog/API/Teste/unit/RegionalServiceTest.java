package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.Teste.unit;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.RegionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.RegionalRepository;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalServiceTest {

    @Mock
    private RegionalRepository regionalRepository;

    @InjectMocks
    private RegionalService regionalService;

    @Test
    @DisplayName("Deve salvar regional manual com externalId nulo")
    void create_ShouldSaveManualRegional() {
        String nome = "Regional Norte";
        Regional saved = new Regional();
        saved.setId(1L);
        saved.setNome(nome);
        saved.setAtivo(true);

        when(regionalRepository.save(any(Regional.class))).thenReturn(saved);

        Regional result = regionalService.create(nome);

        assertNotNull(result);
        assertNull(result.getExternalId());
        assertEquals(nome, result.getNome());
        verify(regionalRepository).save(any());
    }

    @Test
    @DisplayName("Deve buscar apenas regionais inativas")
    void findByStatus_ShouldReturnInactiveRegionals() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Regional> page = new PageImpl<>(List.of(new Regional()));

        when(regionalRepository.findByStatus(false, pageable)).thenReturn(page);

        Page<Regional> result = regionalService.listByStatus(false, pageable);

        assertFalse(result.isEmpty());
        verify(regionalRepository).findByStatus(false, pageable);
    }
}
