package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.ProductEntity;

public interface ProductDAO extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAll(Pageable pageable);

}
