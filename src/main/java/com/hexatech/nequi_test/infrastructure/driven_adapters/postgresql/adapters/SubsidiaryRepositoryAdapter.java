package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.adapters;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.ports.ISubsidiaryRepository;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Subsidiary;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos.SubsidiaryDAO;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.SubsidiaryEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers.EntityMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SubsidiaryRepositoryAdapter implements ISubsidiaryRepository {
    private final SubsidiaryDAO dao;
    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    @Override
    public Mono<Subsidiary> findById(Long id) {
        return Mono.fromCallable(() -> dao.findById(id)
                .map(mapper::toSubsidiary)
                .orElse(null))
                .onErrorMap(e -> new ExternalApplicationException("message.error.external", e))
                .flatMap(subsidiary -> subsidiary != null ? Mono.just(subsidiary) : Mono.empty());
    }

    @Override
    public Mono<Subsidiary> save(Subsidiary franchise) {
        return Mono.fromCallable(() -> {
            SubsidiaryEntity saved =  mapper.toSubsidiayEntity(franchise);
            saved = dao.save(saved);
            return mapper.toSubsidiary(saved);
        })
                .onErrorMap(e -> new ExternalApplicationException("message.error.external", e));
    }

    @Override
    public Flux<Subsidiary> findAllByFranchiseId(Long franchiseId) {
        return Flux.defer(() -> {
            try {
                List<SubsidiaryEntity> entities = dao.findAllByFranchiseId(franchiseId);
                if (entities == null || entities.isEmpty()) {
                    return Flux.empty();
                }
                return Flux.fromIterable(entities)
                        .map(mapper::toSubsidiary);
            } catch (Exception e) {
                return Flux.error(new ExternalApplicationException(
                        "message.error.external" + franchiseId, e));
            }
        });
    }

    @Override
    public Page<Subsidiary> findAll(PaginationParameters paginationParameters) {
        Sort sort = paginationParameters.getSortAscending()
                ? Sort.by(paginationParameters.getSortBy()).ascending()
                : Sort.by(paginationParameters.getSortBy()).descending();
        Pageable pageable = PageRequest.of(
                paginationParameters.getPage() - 1,
                paginationParameters.getPageSize(),
                sort);
        return dao.findAll(pageable)
                .map(mapper::toSubsidiary);
    }

    @Override
    public Mono<Void> delete(Subsidiary franchise) {
        return Mono.fromRunnable(() -> {
            try {
                dao.delete(mapper.toSubsidiayEntity(franchise));
            } catch (Exception e) {
                throw new ExternalApplicationException("message.error.external", e);
            }
        }).onErrorMap(e -> new ExternalApplicationException("message.error.external", e))
                .then();
    }

}
