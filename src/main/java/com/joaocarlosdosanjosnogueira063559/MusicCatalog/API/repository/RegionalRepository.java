package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    Optional<Regional> findByExternalIdAndAtivoTrue(Long externalId);

    @Query("SELECT r FROM Regional r WHERE r.ativo = :status")
    Page<Regional> findByStatus(@Param("status") boolean status, Pageable pageable);

    @Modifying
    @Query("UPDATE Regional r SET r.ativo = false WHERE r.externalId NOT IN :ids AND r.ativo = true")
    void inativarSeNaoPresentes(@Param("ids") List<Long> ids);
}