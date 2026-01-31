package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public class AlbumResponseDTO {

    private Long id;
    private String title;
    @Schema(description = "Arquivo de imagem da capa")
    private String coverUrl;
    @Schema(description = "Lista de IDs dos artistas", example = "[1]")
    private Set<ArtistResponseDTO> artists;

    public AlbumResponseDTO() {}

    public AlbumResponseDTO(Long id, String title, String coverUrl, Set<ArtistResponseDTO> artists) {
        this.id = id;
        this.title = title;
        this.coverUrl = coverUrl;
        this.artists = artists;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Set<ArtistResponseDTO> getArtists() {
        return artists;
    }

    public void setArtists(Set<ArtistResponseDTO> artists) {
        this.artists = artists;
    }
}