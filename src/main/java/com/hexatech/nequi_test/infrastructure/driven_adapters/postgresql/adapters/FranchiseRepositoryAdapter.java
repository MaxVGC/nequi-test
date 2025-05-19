package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.adapters;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.ports.IFranchiseRepository;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos.FranchiseDAO;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.FranchiseEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers.EntityMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements IFranchiseRepository {
    private final FranchiseDAO dao;
    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    @Override
    public Mono<Franchise> findById(Long id) {
        return Mono.fromCallable(() -> dao.findById(id)
                .map(mapper::toFranchise)
                .orElse(null))
                .onErrorMap(e -> new ExternalApplicationException("message.error.external" + id, e))
                .flatMap(franchise -> franchise != null ? Mono.just(franchise) : Mono.empty());
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.fromCallable(() -> {
            FranchiseEntity saved = dao.save(mapper.toFranchiseEntity(franchise));
            return mapper.toFranchise(saved);
        })
                .onErrorMap(e -> new ExternalApplicationException("message.error.external", e));
    }

    @Override
    public Page<Franchise> findAll(PaginationParameters paginationParameters) {
        Sort sort = paginationParameters.getSortAscending()
                ? Sort.by(paginationParameters.getSortBy()).ascending()
                : Sort.by(paginationParameters.getSortBy()).descending();
        Pageable pageable = PageRequest.of(
                paginationParameters.getPage() - 1,
                paginationParameters.getPageSize(),
                sort);
        return dao.findAll(pageable)
                .map(mapper::toFranchise);
    }

    @Override
    public Mono<Void> delete(Franchise franchise) {
        return Mono.fromRunnable(() -> {
            try {
                dao.delete(mapper.toFranchiseEntity(franchise));
            } catch (Exception e) {
                throw new ExternalApplicationException("message.error.external", e);
            }
        })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> new ExternalApplicationException("message.error.external", e))
                .then();
    }
}
