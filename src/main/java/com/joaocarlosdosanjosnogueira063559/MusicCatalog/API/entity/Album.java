package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_album")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título do álbum é obrigatório")
    @Size(min = 2, max = 150, message = "O título deve ter entre 2 e 150 caracteres")
    @Column(nullable = false, length = 150)
    private String title;

    private String coverImageId;

    @NotEmpty(message = "O álbum deve ter pelo menos um artista")
    @ManyToMany
    @JoinTable(
            name = "tb_artist_album",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists = new HashSet<>();

    public Album() {

    }

    public Album(Set<Artist> artists, String coverImageId, String title, Long id) {
        this.artists = artists;
        this.coverImageId = coverImageId;
        this.title = title;
        this.id = id;
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public String getCoverImageId() {
        return coverImageId;
    }

    public void setCoverImageId(String coverImageId) {
        this.coverImageId = coverImageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
