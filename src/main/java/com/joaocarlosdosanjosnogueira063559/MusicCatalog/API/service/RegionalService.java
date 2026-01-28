package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.RegionalExternalDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.RegionalRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegionalService {
    private final RegionalRepository regionalRepository;
    private final RestTemplate restTemplate;

    public RegionalService(RegionalRepository regionalRepository, RestTemplateBuilder builder) {
        this.regionalRepository = regionalRepository;
        this.restTemplate = builder.build();
    }

    @Transactional
    public void syncRegionais() {
        String url = "https://integrador-argus-api.geia.vip/v1/regionais";

        RegionalExternalDTO[] response = restTemplate.getForObject(url, RegionalExternalDTO[].class);
        if (response == null) return;

        Map<Long, RegionalExternalDTO> mapExternas = Arrays.stream(response)
                .collect(Collectors.toMap(RegionalExternalDTO::id, Function.identity()));

        List<Regional> ativasLocais = regionalRepository.findByAtivoTrue();
        Map<Long, Regional> mapLocais = ativasLocais.stream()
                .collect(Collectors.toMap(Regional::getExternalId, Function.identity()));

        mapExternas.values().forEach(externa -> {
            Regional local = mapLocais.get(externa.id());

            if (local == null) {
                Regional novo = new Regional(externa.id(), externa.nome(), true);
                regionalRepository.save(novo);
            } else {
                if (!local.getNome().equals(externa.nome())) {
                    local.setAtivo(false);
                    regionalRepository.save(local);

                    Regional novoVersao = new Regional(externa.id(), externa.nome(), true);
                    regionalRepository.save(novoVersao);
                }
            }
        });

        ativasLocais.forEach(local -> {
            if (!mapExternas.containsKey(local.getExternalId())) {
                local.setAtivo(false);
                regionalRepository.save(local);
            }
        });
    }
}
