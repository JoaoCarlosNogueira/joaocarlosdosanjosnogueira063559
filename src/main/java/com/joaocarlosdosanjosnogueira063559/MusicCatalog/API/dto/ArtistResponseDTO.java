package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto;

public class ArtistResponseDTO {

    private Long id;
    private String name;

    public ArtistResponseDTO() {}

    public ArtistResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}