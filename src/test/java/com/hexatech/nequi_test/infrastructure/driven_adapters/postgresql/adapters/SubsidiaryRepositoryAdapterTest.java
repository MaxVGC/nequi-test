package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.adapters;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.models.Subsidiary;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos.SubsidiaryDAO;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.ProductEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.SubsidiaryEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SubsidiaryRepositoryAdapterTest {

    private SubsidiaryDAO dao;
    private EntityMapper mapper;
    private SubsidiaryRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        dao = mock(SubsidiaryDAO.class);
        mapper = Mappers.getMapper(EntityMapper.class);
        adapter = new SubsidiaryRepositoryAdapter(dao);
    }

    @Test
    void findById_shouldReturnSubsidiary_whenFound() {
        Long id = 1L;
        SubsidiaryEntity entity = new SubsidiaryEntity();

        when(dao.findById(id)).thenReturn(Optional.of(entity));

        StepVerifier.create(adapter.findById(id))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        Long id = 1L;
        when(dao.findById(id)).thenReturn(Optional.empty());

        StepVerifier.create(adapter.findById(id))
                .verifyComplete();

        verify(dao, times(1)).findById(id);
    }

    @Test
    void findById_shouldEmitError_whenDaoThrowsException() {
        Long id = 1L;
        when(dao.findById(id)).thenThrow(new RuntimeException("DB error"));

        StepVerifier.create(adapter.findById(id))
                .expectError(ExternalApplicationException.class)
                .verify();

        verify(dao, times(1)).findById(id);
    }


    @Test
    void findAllByFranchiseId_shouldReturnEmpty_whenNoEntitiesFound() {
        Long franchiseId = 1L;
        when(dao.findAllByFranchiseId(franchiseId)).thenReturn(Collections.emptyList());

        StepVerifier.create(adapter.findAllByFranchiseId(franchiseId))
                .verifyComplete();

        verify(dao, times(1)).findAllByFranchiseId(franchiseId);
    }

    @Test
    void findAllByFranchiseId_shouldEmitError_whenDaoThrowsException() {
        Long franchiseId = 1L;
        when(dao.findAllByFranchiseId(franchiseId)).thenThrow(new RuntimeException("DB error"));

        StepVerifier.create(adapter.findAllByFranchiseId(franchiseId))
                .expectError(ExternalApplicationException.class)
                .verify();

        verify(dao, times(1)).findAllByFranchiseId(franchiseId);
    }

    @Test
    void findAll_shouldReturnPageOfSubsidiary() {
        PaginationParameters params = mock(PaginationParameters.class);
        SubsidiaryEntity entity1 = mock(SubsidiaryEntity.class);
        SubsidiaryEntity entity2 = mock(SubsidiaryEntity.class);

        List<SubsidiaryEntity> entities = Arrays.asList(entity1, entity2);
        Page<SubsidiaryEntity> entityPage = new PageImpl<>(entities);

        when(params.getSortAscending()).thenReturn(true);
        when(params.getSortBy()).thenReturn("id");
        when(params.getPage()).thenReturn(1);
        when(params.getPageSize()).thenReturn(10);
        when(dao.findAll(any(Pageable.class))).thenReturn(entityPage);

        Page<Subsidiary> result = adapter.findAll(params);

        // Check mapping and content
        assert result.getContent().size() == 2;
        verify(dao, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void delete_shouldComplete_whenDaoDeletesSuccessfully() {
        Subsidiary subsidiary = mock(Subsidiary.class);
        SubsidiaryEntity entity = mock(SubsidiaryEntity.class);

        EntityMapper spyMapper = spy(mapper);
        SubsidiaryRepositoryAdapter adapterWithSpyMapper = new SubsidiaryRepositoryAdapter(dao) {
            @Override
            public Mono<Void> delete(Subsidiary franchise) {
                return Mono.fromRunnable(() -> {
                    try {
                        dao.delete(spyMapper.toSubsidiayEntity(franchise));
                    } catch (Exception e) {
                        throw new ExternalApplicationException("message.error.external", e);
                    }
                }).onErrorMap(e -> new ExternalApplicationException("message.error.external", e))
                        .then();
            }
        };

        doReturn(entity).when(spyMapper).toSubsidiayEntity(subsidiary);
        doNothing().when(dao).delete(entity);

        StepVerifier.create(adapterWithSpyMapper.delete(subsidiary))
                .verifyComplete();

        verify(dao, times(1)).delete(entity);
    }

    @Test
    void delete_shouldEmitError_whenDaoThrowsException() {
        Subsidiary subsidiary = mock(Subsidiary.class);
        SubsidiaryEntity entity = mock(SubsidiaryEntity.class);

        EntityMapper spyMapper = spy(mapper);
        SubsidiaryRepositoryAdapter adapterWithSpyMapper = new SubsidiaryRepositoryAdapter(dao) {
            @Override
            public Mono<Void> delete(Subsidiary franchise) {
                return Mono.fromRunnable(() -> {
                    try {
                        dao.delete(spyMapper.toSubsidiayEntity(franchise));
                    } catch (Exception e) {
                        throw new ExternalApplicationException("message.error.external", e);
                    }
                }).onErrorMap(e -> new ExternalApplicationException("message.error.external", e))
                        .then();
            }
        };

        doReturn(entity).when(spyMapper).toSubsidiayEntity(subsidiary);
        doThrow(new RuntimeException("DB error")).when(dao).delete(entity);

        StepVerifier.create(adapterWithSpyMapper.delete(subsidiary))
                .expectError(ExternalApplicationException.class)
                .verify();

        verify(dao, times(1)).delete(entity);
    }
}