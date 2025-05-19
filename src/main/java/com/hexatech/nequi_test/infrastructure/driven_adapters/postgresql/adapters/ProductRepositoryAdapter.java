package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.adapters;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.ports.IProductRepository;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.domain.models.Subsidiary;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos.ProductDAO;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.ProductEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers.EntityMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements IProductRepository {
    private final ProductDAO dao;
    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    @Override
    public Mono<Product> findById(Long id) {
        return Mono.fromCallable(() -> dao.findById(id)
                .map(mapper::toProduct)
                .orElse(null))
                .onErrorMap(e -> new ExternalApplicationException("message.error.external" + id, e))
                .flatMap(product -> product != null ? Mono.just(product) : Mono.empty());
    }

    @Override
    public Mono<Product> save(Product product) {
        return Mono.fromCallable(() -> {
            ProductEntity saved = mapper.toProductEntity(product);
            saved = dao.save(saved);
            return mapper.toProduct(saved);
        })
                .onErrorMap(e -> new ExternalApplicationException("message.error.external", e));
    }

    @Override
    public Page<Product> findAll(PaginationParameters paginationParameters) {
        Sort sort = paginationParameters.getSortAscending()
                ? Sort.by(paginationParameters.getSortBy()).ascending()
                : Sort.by(paginationParameters.getSortBy()).descending();
        Pageable pageable = PageRequest.of(
                paginationParameters.getPage() - 1,
                paginationParameters.getPageSize(),
                sort);
        return dao.findAll(pageable)
                .map(mapper::toProduct);
    }

    @Override
    public Mono<Void> delete(Product franchise) {
        return Mono.fromRunnable(() -> {
            try {
                dao.delete(mapper.toProductEntity(franchise));
            } catch (Exception e) {
                throw new ExternalApplicationException("Error while deleting product", e);
            }
        }).onErrorMap(e -> new ExternalApplicationException("Error while deleting product", e))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }
}
