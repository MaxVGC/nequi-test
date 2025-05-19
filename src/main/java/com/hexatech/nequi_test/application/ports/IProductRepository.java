package com.hexatech.nequi_test.application.ports;

import org.springframework.data.domain.Page;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.domain.models.Product;

import reactor.core.publisher.Mono;

public interface IProductRepository {
    Mono<Product> findById(Long id);

    Mono<Product> save(Product franchise);

    Page<Product> findAll(PaginationParameters paginationParameters);

    Mono<Void> delete(Product franchise);

}
