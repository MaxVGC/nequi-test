package com.hexatech.nequi_test.application.ports;

import org.springframework.data.domain.Page;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.domain.models.Franchise;

import reactor.core.publisher.Mono;

public interface IFranchiseRepository {
    Mono<Franchise> findById(Long id);

    Mono<Franchise> save(Franchise franchise);

    Page<Franchise> findAll(PaginationParameters paginationParameters);

    Mono<Void> delete(Franchise franchise);
}
