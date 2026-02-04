package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.Teste.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.auth.AuthenticationRequest;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.auth.AuthenticationResponse;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.RegionalExternalDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.User;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.RegionalRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RegionalControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RegionalRepository regionalRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private RestTemplate restTemplate;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        regionalRepository.deleteAll();

        User user = new User();
        user.setEmail("admin@music.com");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setRole("ADMIN");
        userRepository.save(user);

        this.token = "Bearer " + getAccessToken("admin@music.com", "12345");
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
    @DisplayName("Cenário: Sincronizar regionais com API externa simulada")
    void deveSincronizarRegionaisComSucesso() throws Exception {
        RegionalExternalDTO[] mockResponse = {
                new RegionalExternalDTO(100L, "Sudeste"),
                new RegionalExternalDTO(200L, "Sul")
        };
        when(restTemplate.getForObject(anyString(), eq(RegionalExternalDTO[].class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/v1/regionais/sync")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/regionais")
                        .header("Authorization", token))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].nome").value("Sudeste"));
    }

    @Test
    @DisplayName("Cenário: Listar apenas regionais ativas (Filtro por Status)")
    void deveListarApenasAtivas() throws Exception {
        regionalRepository.save(new Regional(1L, "Ativa", true));
        regionalRepository.save(new Regional(2L, "Inativa", false));

        mockMvc.perform(get("/v1/regionais/status")
                        .param("status", "true")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Ativa"));
    }

    @Test
    @DisplayName("Cenário: Atualizar status de uma regional")
    void deveAtualizarStatus() throws Exception {
        Regional reg = regionalRepository.save(new Regional(1L, "Brasília", true));

        mockMvc.perform(patch("/v1/regionais/" + reg.getId() + "/status")
                        .param("status", "false")
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/regionais/status")
                        .param("status", "false")
                        .header("Authorization", token))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Brasília"));
    }
}