package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.controller;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.AlbumResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/albums")
@Tag(name = "Álbuns", description = "Gerenciamento de álbuns e capas")
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cria álbum com upload de capa", description = "Envia dados do álbum")
    public AlbumResponseDTO create( @RequestParam("title") String title,@RequestParam("artistId") List<Long> artistIds, @RequestParam("file") MultipartFile file) {
        return albumService.create(title, artistIds, file);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualiza o álbum", description = "Permite atualizar título, artistas e a imagem da capa.")
    public AlbumResponseDTO update(@PathVariable Long id, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "artistIds", required = false) List<Long> artistIds,
                                   @RequestParam(value = "file", required = false) MultipartFile file) {
        return albumService.update(id, title, artistIds, file);
    }

    @GetMapping
    @Operation(summary = "Lista álbuns paginados",
            description = "Permite filtrar por nome do artista e paginação")
    public Page<AlbumResponseDTO> findAll(@RequestParam(required = false) String artistName, @ParameterObject @PageableDefault(sort = "title", direction = Sort.Direction.ASC) Pageable pageable)
    {
        return albumService.findAll(artistName, pageable);
    }
}