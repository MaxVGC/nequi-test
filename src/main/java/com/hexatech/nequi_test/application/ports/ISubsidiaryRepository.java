package com.hexatech.nequi_test.application.ports;

import org.springframework.data.domain.Page;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.domain.models.Subsidiary;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISubsidiaryRepository {
    Mono<Subsidiary> findById(Long id);

    Mono<Subsidiary> save(Subsidiary subsidiary);

    Flux<Subsidiary> findAllByFranchiseId(Long franchiseId);

    Page<Subsidiary> findAll(PaginationParameters paginationParameters);

    Mono<Void> delete(Subsidiary franchise);

}
