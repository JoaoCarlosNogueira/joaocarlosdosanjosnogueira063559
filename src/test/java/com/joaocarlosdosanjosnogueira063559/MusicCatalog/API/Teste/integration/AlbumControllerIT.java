package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.Teste.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.auth.AuthenticationRequest;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.auth.AuthenticationResponse;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Artist;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.User;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.ArtistRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.UserRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.MinioService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Novo padrão 3.4+
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AlbumControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ArtistRepository artistRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private MinioService minioService;
    @MockitoBean private SimpMessagingTemplate messagingTemplate;

    private String token;
    private Artist artist;

    @BeforeEach
    void setup() throws Exception {
        // Limpeza e preparação de dados para garantir isolamento
        userRepository.deleteAll();
        artistRepository.deleteAll();

        User user = new User();
        user.setEmail("admin@test.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole("ADMIN");
        userRepository.save(user);

        artist = new Artist();
        artist.setName("Arctic Monkeys");
        artist = artistRepository.save(artist);

        // Login centralizado
        this.token = "Bearer " + getAccessToken("admin@test.com", "123456");

        // Mock do Storage
        when(minioService.uploadFile(any())).thenReturn("mock-file-id");
    }

    private String getAccessToken(String email, String password) throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest(email, password);
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, AuthenticationResponse.class).getAccessToken();
    }

    @Test
    @DisplayName("Deve criar álbum com múltiplos artistas e upload de capa")
    void deveCriarAlbumCompleto() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "am.png", "image/png", "content".getBytes());

        mockMvc.perform(multipart("/v1/albums")
                        .file(file)
                        .param("title", "AM")
                        .param("artistId", artist.getId().toString())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("AM"))
                .andExpect(jsonPath("$.artists[0].name").value("Arctic Monkeys"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar álbum com artista inexistente")
    void deveRetornarErroArtistaInexistente() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "t.jpg", "image/jpeg", new byte[10]);

        mockMvc.perform(multipart("/v1/albums")
                        .file(file)
                        .param("title", "Album Fantasma")
                        .param("artistId", "9999") // ID que não existe
                        .header("Authorization", token))
                .andExpect(status().isInternalServerError()); // Baseado no seu throw RuntimeException
    }
}