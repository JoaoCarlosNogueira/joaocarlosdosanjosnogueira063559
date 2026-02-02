package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.controller;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.RegionalRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.RegionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/regionais")
@RequiredArgsConstructor
@Tag(name = "Regionais", description = "Endpoints para gerenciamento e sincronização de regionais")
@SecurityRequirement(name = "bearerAuth")
public class RegionalController {

    private final RegionalService regionalService;
    private final RegionalRepository regionalRepository;

    @PostMapping("/sync")
    @Operation(summary = "Sincronização Automática", description = "Executa as regras de negócio baseadas na API Argus (Novo/Ausente/Alterado).")
    public ResponseEntity<Void> sync() {
        regionalService.syncRegionais();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Operation(summary = "Criar Regional Manualmente", description = "Criação interna. O externalId é gerenciado pelo sistema.")
    public ResponseEntity<Regional> create(@RequestParam String nome) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionalService.create(nome));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar Status", description = "Altera o atributo 'ativo' usando o ID interno do registro.")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam boolean status) {
        regionalService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar todas (Paginado)", description = "Retorna o histórico completo de regionais com paginação.")
    public ResponseEntity<Page<Regional>> listAll(@ParameterObject @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(regionalRepository.findAll(pageable));
    }

    @GetMapping("/status")
    @Operation(summary = "Filtrar por Status", description = "Lista as regionais baseadas no status (true para ativa, false para inativa).")
    public ResponseEntity<Page<Regional>> listByStatus(
            @RequestParam(name = "status", defaultValue = "true") boolean status,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(regionalService.listByStatus(status, pageable));
    }
}