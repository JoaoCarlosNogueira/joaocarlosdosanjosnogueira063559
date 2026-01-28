package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.controller;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.RegionalRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.RegionalService; // Import ajustado
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/regionais")
@Tag(name = "Regionais", description = "Integração e consulta de Regionais (API Externa)")
@SecurityRequirement(name = "bearerAuth")
public class RegionalController {

    private final RegionalService regionalService;
    private final RegionalRepository regionalRepository;

    public RegionalController(RegionalService regionalService, RegionalRepository regionalRepository) {
        this.regionalService = regionalService;
        this.regionalRepository = regionalRepository;
    }

    @PostMapping("/sync")
    @Operation(summary = "Força a sincronização manual com a API externa",
            description = "Busca dados do integrador, atualiza registros existentes e inativa os removidos.")
    public ResponseEntity<String> sync() {
        try {
            // Chamada ajustada para usar o seu service
            regionalService.syncRegionais();
            return ResponseEntity.ok("Sincronização realizada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao sincronizar: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Lista regionais ativas (Paginado)",
            description = "Retorna apenas as regionais marcadas como 'ativo=true' no banco local.")
    public Page<Regional> findAll(
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        return regionalRepository.findByAtivoTrue(pageable);
    }
}