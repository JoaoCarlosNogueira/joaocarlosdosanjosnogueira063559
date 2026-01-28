package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.controller;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.AlbumResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria álbum com upload de capa",
            description = "Envia dados do álbum e arquivo de imagem simultaneamente")
    public AlbumResponseDTO create(
            @Parameter(description = "Título do Álbum")
            @RequestParam("title") String title,

            @Parameter(description = "Lista de IDs dos artistas (ex: 1,2,3)")
            @RequestParam("artistIds") List<Long> artistIds,

            @Parameter(description = "Arquivo de imagem da capa (.jpg, .png)", content = @Content(mediaType = "application/octet-stream"))
            @RequestParam("file") MultipartFile file) {

        return albumService.create(title, artistIds, file);
    }

    @GetMapping
    @Operation(summary = "Lista álbuns paginados",
            description = "Permite filtrar por nome do artista e paginação")
    public Page<AlbumResponseDTO> findAll(
            @RequestParam(required = false) String artistName,
            @PageableDefault(sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {

        return albumService.findAll(artistName, pageable);
    }
}