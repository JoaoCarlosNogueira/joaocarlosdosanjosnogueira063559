package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.Teste.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth.AuthenticationRequestDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth.AuthenticationResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.ArtistResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.User;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ArtistControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ArtistRepository artistRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        artistRepository.deleteAll();

        User user = new User();
        user.setEmail("admin@music.com");
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setRole("ADMIN");
        userRepository.save(user);

        this.token = "Bearer " + getAccessToken("admin@music.com", "senha123");
    }

    private String getAccessToken(String email, String password) throws Exception {
        AuthenticationRequestDTO authRequest = new AuthenticationRequestDTO(email, password);
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, AuthenticationResponseDTO.class).getAccessToken();
    }

    @Test
    @DisplayName("Cenário: Criar um novo artista com sucesso")
    void deveCriarArtista() throws Exception {
        ArtistResponseDTO request = new ArtistResponseDTO(null, "Charlie Brown Jr");

        mockMvc.perform(post("/v1/artists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charlie Brown Jr"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("Cenário: Atualizar artista existente")
    void deveAtualizarArtista() throws Exception {
        // Preparar dado
        Artist artist = new Artist();
        artist.setName("Legião Urbana");
        artist = artistRepository.save(artist);

        ArtistResponseDTO updateRequest = new ArtistResponseDTO(null, "Legião Urbana (Oficial)");

        mockMvc.perform(put("/v1/artists/" + artist.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Legião Urbana (Oficial)"));
    }

    @Test
    @DisplayName("Cenário: Tentar acessar sem token deve retornar 403")
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/v1/artists"))
                .andExpect(status().isForbidden());
    }
}