package com.hexatech.nequi_test.application.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.ap.internal.util.Collections;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewProductRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchProductNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchStockRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;
import com.hexatech.nequi_test.application.ports.IProductRepository;
import com.hexatech.nequi_test.application.ports.ISubsidiaryRepository;
import com.hexatech.nequi_test.domain.exceptions.NotExistProductException;
import com.hexatech.nequi_test.domain.exceptions.NotExistSubsidiaryException;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.domain.models.Subsidiary;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private IProductRepository productRepository;
    @Mock
    private ISubsidiaryRepository subsidiaryRepository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void addProductToSubsidiary_success() {
        Long subsId = 1L;
        NewProductRequestDTO request = new NewProductRequestDTO("Prod1", 10, subsId);

        Subsidiary subs = new Subsidiary();
        subs.setId(subsId);
        subs.setName("Sub1");

        given(subsidiaryRepository.findById(subsId)).willReturn(Mono.just(subs));
        given(productRepository.save(any(Product.class))).willReturn(Mono.just(new Product()));

        Mono<Void> result = service.addProductToSubsidiary(request);

        StepVerifier.create(result)
                .verifyComplete();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProductToSubsidiary_subsidiaryNotFound() {
        Long subsId = 2L;
        NewProductRequestDTO request = new NewProductRequestDTO("Prod", 10, subsId);
        given(subsidiaryRepository.findById(subsId)).willReturn(Mono.empty());

        Mono<Void> result = service.addProductToSubsidiary(request);

        StepVerifier.create(result)
                .expectError(NotExistSubsidiaryException.class)
                .verify();
    }

    @Test
    void patchProductName_success() {
        Long id = 1L;
        PatchProductNameRequestDTO request = new PatchProductNameRequestDTO(id, "New Name");
        Product product = new Product();
        product.setId(id);
        product.setName("Old");

        given(productRepository.findById(id)).willReturn(Mono.just(product));
        given(productRepository.save(product)).willReturn(Mono.just(product));

        Mono<Void> result = service.patchProductName(request);

        StepVerifier.create(result)
                .verifyComplete();
        verify(productRepository).save(product);
    }

    @Test
    void patchProductName_notFound() {
        Long id = 3L;
        PatchProductNameRequestDTO request = new PatchProductNameRequestDTO(id, "Name");
        given(productRepository.findById(id)).willReturn(Mono.empty());

        Mono<Void> result = service.patchProductName(request);

        StepVerifier.create(result)
                .expectError(NotExistProductException.class)
                .verify();
    }

    @Test
    void removeProductFromSubsidiary_success() {
        Long subsId = 1L, prodId = 2L;
        Subsidiary subs = new Subsidiary();
        subs.setId(subsId);
        Product product = new Product();
        product.setId(prodId);
        subs.setProducts(List.of(product));

        given(subsidiaryRepository.findById(subsId)).willReturn(Mono.just(subs));
        given(productRepository.delete(any(Product.class))).willReturn(Mono.empty());

        Mono<Void> result = service.removeProductFromSubsidiary(subsId, prodId);

        StepVerifier.create(result)
                .verifyComplete();
        verify(productRepository).delete(any(Product.class));
    }

    @Test
    void removeProductFromSubsidiary_productNotFound() {
        Long subsId = 1L, prodId = 3L;
        Subsidiary subs = new Subsidiary();
        subs.setId(subsId);
        subs.setProducts(List.of());
        given(subsidiaryRepository.findById(subsId)).willReturn(Mono.just(subs));

        Mono<Void> result = service.removeProductFromSubsidiary(subsId, prodId);

        StepVerifier.create(result)
                .expectError(NotExistProductException.class)
                .verify();
    }

    @Test
    void patchStock_success() {
        Long id = 1L;
        PatchStockRequestDTO request = new PatchStockRequestDTO(id, 20);
        Product product = new Product();
        product.setId(id);
        product.setStock(5);

        given(productRepository.findById(id)).willReturn(Mono.just(product));
        given(productRepository.save(product)).willReturn(Mono.just(product));

        Mono<Void> result = service.patchStock(request);

        StepVerifier.create(result)
                .verifyComplete();
        verify(productRepository).save(product);
    }

    @Test
    void getAllPaginated_success() {
        PaginationParameters params = new PaginationParameters();
        Product prod = new Product();
        prod.setId(1L);
        prod.setName("P");
        prod.setStock(1);
        Pageable pageable = PageRequest.of(params.getPage(), 1);
        Page<Product> page = new PageImpl<>(List.of(prod), pageable, 1);

        doReturn(page).when(productRepository).findAll(params);

        Mono<PaginationResult<ProductDTO>> result = service.getAllPaginated(params);

        StepVerifier.create(result)
                .assertNext(p -> {
                    assert p.getData().size() == 1;
                    assert p.getTotalElements() == 2;
                })
                .verifyComplete();
    }
}
