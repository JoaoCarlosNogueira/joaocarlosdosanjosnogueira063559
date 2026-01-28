package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.Regional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    Page<Regional> findByAtivoTrue(Pageable pageable);
    List<Regional> findByAtivoTrue();

    Page<Regional> findByExternalIdAndAtivoTrue(Long externalId, Pageable pageable);
}