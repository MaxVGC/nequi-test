package com.hexatech.nequi_test.application.services.impl;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewFranchiseRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchFranchiseNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.FranchiseDTO;
import com.hexatech.nequi_test.application.dtos.out.MaxProductsFranchiseResponseDTO;
import com.hexatech.nequi_test.application.dtos.out.MaxStockProductBySubsidiaryDTO;
import com.hexatech.nequi_test.application.mappers.RequestMapper;
import com.hexatech.nequi_test.application.mappers.ResponseMapper;
import com.hexatech.nequi_test.application.ports.IFranchiseRepository;
import com.hexatech.nequi_test.domain.exceptions.NotExistFranchiseException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.domain.models.Subsidiary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FranchiseServiceImplTest {

    @Mock
    private IFranchiseRepository franchiseRepository;

    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
    private ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        franchiseService = new FranchiseServiceImpl(franchiseRepository);
    }

    @Test
    void addFranchise_shouldCompleteSuccessfully() {
        NewFranchiseRequestDTO request = new NewFranchiseRequestDTO("Test Franchise");
        Franchise franchise = new Franchise();
        franchise.setName("Test Franchise");

        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseService.addFranchise(request))
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository, times(1)).save(captor.capture());
        assertEquals("Test Franchise", captor.getValue().getName());
    }

    @Test
    void addFranchise_shouldReturnErrorWhenRepositoryFails() {
        NewFranchiseRequestDTO request = new NewFranchiseRequestDTO("Test Franchise");
        RuntimeException repoException = new RuntimeException("DB error");

        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.error(repoException));

        StepVerifier.create(franchiseService.addFranchise(request))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Failed to add franchise") &&
                        throwable.getCause() == repoException)
                .verify();

        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void patchFranchiseName_shouldUpdateNameSuccessfully() {
        PatchFranchiseNameRequestDTO request = new PatchFranchiseNameRequestDTO();
        request.setId(1L);
        request.setNewName("Updated Name");

        Franchise franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Old Name");

        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseService.patchFranchiseName(request))
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(1L);
        verify(franchiseRepository, times(1)).save(franchise);
        assertEquals("Updated Name", franchise.getName());
    }

    @Test
    void patchFranchiseName_shouldReturnErrorWhenFranchiseNotFound() {
        PatchFranchiseNameRequestDTO request = new PatchFranchiseNameRequestDTO();
        request.setId(2L);
        request.setNewName("Updated Name");

        when(franchiseRepository.findById(2L)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.patchFranchiseName(request))
                .expectError(NotExistFranchiseException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById(2L);
        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void getTopProductsByFranchise_shouldReturnTopProducts() {
        Long franchiseId = 1L;
        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);
        franchise.setName("Franchise 1");

        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setStock(10);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setStock(20);

        Subsidiary subsidiary1 = new Subsidiary();
        subsidiary1.setName("Subsidiary 1");
        subsidiary1.setProducts(List.of(product1, product2));

        franchise.setSubsidiaries(List.of(subsidiary1));

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseService.getTopProductsByFranchise(franchiseId))
                .assertNext(response -> {
                    assertEquals("Franchise 1", response.getFranchiseName());
                    assertNotNull(response.getTopProductsBySubsidiary());
                    assertEquals(1, response.getTopProductsBySubsidiary().size());
                    // validamos que el nombre de la subsidiaria y el producto coincidan
                    var dto = response.getTopProductsBySubsidiary().get(0);
                    assertEquals("Subsidiary 1", dto.getSubsidiary().name());
                    assertEquals("Product 2", dto.getProduct().name());
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
    }

    @Test
    void getTopProductsByFranchise_shouldReturnErrorWhenFranchiseNotFound() {
        Long franchiseId = 2L;
        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.getTopProductsByFranchise(franchiseId))
                .expectError(NotExistFranchiseException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
    }

    @Test
    void getAllPaginated_shouldReturnPaginationResult() {
        PaginationParameters params = new PaginationParameters();
        Franchise prod = new Franchise();
        prod.setId(1L);
        prod.setName("P");
        Pageable pageable = PageRequest.of(params.getPage(), 1);
        Page<Franchise> page = new PageImpl<>(List.of(prod), pageable, 1);
        doReturn(page).when(franchiseRepository).findAll(params);

        StepVerifier.create(franchiseService.getAllPaginated(params))
                .assertNext(result -> {
                    assertEquals(2, result.getCurrentPage());
                    assertEquals(1, result.getPageSize());
                    assertEquals(2, result.getTotalPages());
                    assertEquals(2, result.getTotalElements());
                    assertNotNull(result.getData());
                    assertEquals(1, result.getData().size());
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findAll(params);
    }

    @Test
    void getAllPaginated_shouldReturnErrorOnException() {
        PaginationParameters params = new PaginationParameters();
        when(franchiseRepository.findAll(params)).thenThrow(new RuntimeException("DB error"));

        StepVerifier.create(franchiseService.getAllPaginated(params))
                .expectErrorMatches(e -> e instanceof RuntimeException)
                .verify();

        verify(franchiseRepository, times(1)).findAll(params);
    }
}