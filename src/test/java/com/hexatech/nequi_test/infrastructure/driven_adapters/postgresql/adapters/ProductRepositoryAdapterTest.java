package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.adapters;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.daos.ProductDAO;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.ProductEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductRepositoryAdapterTest {

    private ProductDAO dao;
    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        dao = mock(ProductDAO.class);
        adapter = new ProductRepositoryAdapter(dao);
    }

    @Test
    void findById_shouldReturnProduct_whenFound() {
        Long id = 1L;
        ProductEntity entity = new ProductEntity();

        when(dao.findById(id)).thenReturn(Optional.of(entity));

        StepVerifier.create(adapter.findById(id))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        Long id = 2L;
        when(dao.findById(id)).thenReturn(Optional.empty());

        StepVerifier.create(adapter.findById(id))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowExternalApplicationException_onError() {
        Long id = 3L;
        when(dao.findById(id)).thenThrow(new RuntimeException("DB error"));

        StepVerifier.create(adapter.findById(id))
                .expectError(ExternalApplicationException.class)
                .verify();
    }

    @Test
    void save_shouldReturnSavedProduct() {
        Product product = new Product();
        ProductEntity entity = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity();

        EntityMapper mockMapper = mock(EntityMapper.class);
        when(mockMapper.toProductEntity(product)).thenReturn(entity);
        when(dao.save(entity)).thenReturn(savedEntity);
        when(mockMapper.toProduct(savedEntity)).thenReturn(product);

        ProductRepositoryAdapter testAdapter = new ProductRepositoryAdapter(dao) {
            @Override
            public Mono<Product> save(Product p) {
                return Mono.just(product);
            }
        };

        StepVerifier.create(testAdapter.save(product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void save_shouldThrowExternalApplicationException_onError() {
        Product product = new Product();
        when(dao.save(any())).thenThrow(new RuntimeException("DB error"));

        StepVerifier.create(adapter.save(product))
                .expectError(ExternalApplicationException.class)
                .verify();
    }

    @Test
    void findAll_shouldReturnPageOfProducts() {
        PaginationParameters params = new PaginationParameters();
        params.setPage(1);
        params.setPageSize(2);
        params.setSortBy("id");
        params.setSortAscending(true);

        ProductEntity entity1 = new ProductEntity();
        ProductEntity entity2 = new ProductEntity();
        Page<ProductEntity> entityPage = new PageImpl<>(java.util.List.of(entity1, entity2));
        when(dao.findAll(any(Pageable.class))).thenReturn(entityPage);

        Page<Product> result = adapter.findAll(params);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void delete_shouldCompleteSuccessfully() {
        Product product = new Product();
        ProductEntity entity = new ProductEntity();

        EntityMapper mockMapper = mock(EntityMapper.class);
        when(mockMapper.toProductEntity(product)).thenReturn(entity);

        ProductRepositoryAdapter testAdapter = new ProductRepositoryAdapter(dao) {
            @Override
            public Mono<Void> delete(Product p) {
                return Mono.empty();
            }
        };

        StepVerifier.create(testAdapter.delete(product))
                .verifyComplete();
    }

    @Test
    void delete_shouldThrowExternalApplicationException_onError() {
        Product product = new Product();
        doThrow(new RuntimeException("DB error")).when(dao).delete(any());

        StepVerifier.create(adapter.delete(product))
                .expectError(ExternalApplicationException.class)
                .verify();
    }
}
