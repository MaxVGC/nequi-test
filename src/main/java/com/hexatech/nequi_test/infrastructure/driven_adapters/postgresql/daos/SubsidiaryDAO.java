package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.SubsidiaryEntity;

public interface SubsidiaryDAO extends JpaRepository<SubsidiaryEntity, Long> {
    List<SubsidiaryEntity> findAllByFranchiseId(Long franchiseId);
    Page<SubsidiaryEntity> findAll(Pageable pageable);
}
