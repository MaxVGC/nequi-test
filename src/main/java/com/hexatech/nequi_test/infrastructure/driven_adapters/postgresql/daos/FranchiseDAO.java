package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.FranchiseEntity;

public interface FranchiseDAO extends JpaRepository<FranchiseEntity, Long> {
    Page<FranchiseEntity> findAll(Pageable pageable);
}
