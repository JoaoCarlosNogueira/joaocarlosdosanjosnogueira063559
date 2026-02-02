package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.controller;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.ArtistResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/artists")
@Tag(name = "Artistas", description = "Gerenciamento de artistas")
@SecurityRequirement(name = "bearerAuth") // Exige token para tudo aqui
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo artista")
    public ArtistResponseDTO create(@RequestBody ArtistResponseDTO request) {
        return artistService.create(request.getName());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um artista existente", description = "Substitui os dados do artista informado pelo ID.")
    public ArtistResponseDTO update(@PathVariable Long id, @RequestBody ArtistResponseDTO request) {
        return artistService.update(id, request.getName());
    }

    @GetMapping
    @Operation(summary = "Lista artistas paginados e ordenados")
    public Page<ArtistResponseDTO> findAll(
            @ParameterObject
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        return artistService.findAll(pageable);
    }
}