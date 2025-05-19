package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.adapters;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos.FranchiseDAO;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.FranchiseEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseDAO dao;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private FranchiseRepositoryAdapter adapter;

    private Franchise franchise;
    private FranchiseEntity franchiseEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        franchise = new Franchise();
        franchiseEntity = new FranchiseEntity();
        adapter = new FranchiseRepositoryAdapter(dao);
        adapter = spy(adapter);
    }

    @Test
    void findById_shouldReturnFranchise_whenFound() {
        Long id = 1L;
        when(dao.findById(id)).thenReturn(Optional.of(franchiseEntity));
        when(mapper.toFranchise(franchiseEntity)).thenReturn(franchise);

        Mono<Franchise> result = adapter.findById(id);

        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        Long id = 1L;
        when(dao.findById(id)).thenReturn(Optional.empty());

        Mono<Franchise> result = adapter.findById(id);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnError_whenExceptionThrown() {
        Long id = 1L;
        when(dao.findById(id)).thenThrow(new RuntimeException("DB error"));

        Mono<Franchise> result = adapter.findById(id);

        StepVerifier.create(result)
                .expectError(ExternalApplicationException.class)
                .verify();
    }

    @Test
    void save_shouldReturnSavedFranchise() {
        when(mapper.toFranchiseEntity(franchise)).thenReturn(franchiseEntity);
        when(dao.save(franchiseEntity)).thenReturn(franchiseEntity);
        when(mapper.toFranchise(franchiseEntity)).thenReturn(franchise);

        Mono<Franchise> result = adapter.save(franchise);

        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void save_shouldReturnError_whenExceptionThrown() {
        when(mapper.toFranchiseEntity(franchise)).thenReturn(franchiseEntity);
        when(dao.save(franchiseEntity)).thenThrow(new RuntimeException("DB error"));

        Mono<Franchise> result = adapter.save(franchise);

        StepVerifier.create(result)
                .expectError(ExternalApplicationException.class)
                .verify();
    }

    @Test
    void findAll_shouldReturnPageOfFranchises() {
        PaginationParameters params = new PaginationParameters();
        params.setPage(1);
        params.setPageSize(10);
        params.setSortBy("id");
        params.setSortAscending(true);

        FranchiseEntity entity = new FranchiseEntity();
        Franchise domain = new Franchise();
        Page<FranchiseEntity> entityPage = new PageImpl<>(Collections.singletonList(entity));
        when(dao.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(mapper.toFranchise(entity)).thenReturn(domain);

        Page<Franchise> result = adapter.findAll(params);

        assertEquals(1, result.getContent().size());
        assertEquals(domain, result.getContent().get(0));
    }

    @Test
    void delete_shouldComplete_whenSuccess() {
        when(mapper.toFranchiseEntity(franchise)).thenReturn(franchiseEntity);
        doNothing().when(dao).delete(franchiseEntity);

        Mono<Void> result = adapter.delete(franchise);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void delete_shouldReturnError_whenExceptionThrown() {
        when(mapper.toFranchiseEntity(franchise)).thenReturn(franchiseEntity);
        doThrow(new RuntimeException("DB error")).when(dao).delete(franchiseEntity);

        Mono<Void> result = adapter.delete(franchise);

        StepVerifier.create(result)
                .expectError(ExternalApplicationException.class)
                .verify();
    }
}
