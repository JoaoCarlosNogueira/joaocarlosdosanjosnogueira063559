package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.RegionalExternalDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.RegionalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionalService {
    private final RegionalRepository repository;
    private final RestTemplate restTemplate;

    @Transactional
    public void syncRegionais() {
        String url = "https://integrador-argus-api.geia.vip/v1/regionais";
        RegionalExternalDTO[] response = restTemplate.getForObject(url, RegionalExternalDTO[].class);

        if (response == null) return;

        List<Long> idsExternos = Arrays.stream(response).map(RegionalExternalDTO::id).toList();

        repository.inativarSeNaoPresentes(idsExternos);

        for (RegionalExternalDTO externa : response) {
            Optional<Regional> localAtiva = repository.findByExternalIdAndAtivoTrue(externa.id());

            if (localAtiva.isEmpty()) {
                repository.save(new Regional(externa.id(), externa.nome(), true));
            } else if (!localAtiva.get().getNome().equals(externa.nome())) {
                Regional antiga = localAtiva.get();
                antiga.setAtivo(false);
                repository.save(antiga);

                repository.save(new Regional(externa.id(), externa.nome(), true));
            }
        }
    }

    @Transactional
    public Regional create(String nome) {
        Regional nova = new Regional();
        nova.setNome(nome);
        nova.setAtivo(true);
        nova.setExternalId(null);
        return repository.save(nova);
    }

    @Transactional
    public void updateStatus(Long id, boolean status) {
        repository.findById(id).ifPresent(r -> {
            r.setAtivo(status);
            repository.save(r);
        });
    }

    @Transactional
    public Page<Regional> listByStatus(boolean status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }
}