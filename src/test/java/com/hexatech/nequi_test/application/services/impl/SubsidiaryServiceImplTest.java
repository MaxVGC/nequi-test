package com.hexatech.nequi_test.application.services.impl;
import com.hexatech.nequi_test.application.dtos.in.NewSubsidiaryRequestDTO;
import com.hexatech.nequi_test.application.mappers.RequestMapper;
import com.hexatech.nequi_test.application.ports.IFranchiseRepository;
import com.hexatech.nequi_test.application.ports.ISubsidiaryRepository;
import com.hexatech.nequi_test.domain.exceptions.NotExistFranchiseException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Subsidiary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




class SubsidiaryServiceImplTest {

    private ISubsidiaryRepository subsidiaryRepository;
    private IFranchiseRepository franchiseRepository;
    private RequestMapper requestMapper;
    private SubsidiaryServiceImpl service;

    @BeforeEach
    void setUp() {
        subsidiaryRepository = mock(ISubsidiaryRepository.class);
        franchiseRepository = mock(IFranchiseRepository.class);
        requestMapper = Mappers.getMapper(RequestMapper.class);
        service = new SubsidiaryServiceImpl(subsidiaryRepository, franchiseRepository);
    }

    @Test
    void addSubsidiaryToFranchise_shouldAddSubsidiary_whenFranchiseExists() {
        // Arrange
        Long franchiseId = 1L;
        NewSubsidiaryRequestDTO request = new NewSubsidiaryRequestDTO();
        request.setFranchiseId(franchiseId);
        request.setName("Test Subsidiary");

        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);
        franchise.setName("Test Franchise");

        Subsidiary subsidiary = new Subsidiary();
        subsidiary.setName(request.getName());

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenReturn(Mono.just(subsidiary));

        // Act & Assert
        StepVerifier.create(service.addSubsidiaryToFranchise(request))
                .verifyComplete();

        ArgumentCaptor<Subsidiary> captor = ArgumentCaptor.forClass(Subsidiary.class);
        verify(subsidiaryRepository).save(captor.capture());
        assertEquals(request.getName(), captor.getValue().getName());
    }

    @Test
    void addSubsidiaryToFranchise_shouldError_whenFranchiseDoesNotExist() {
        // Arrange
        Long franchiseId = 2L;
        NewSubsidiaryRequestDTO request = new NewSubsidiaryRequestDTO();
        request.setFranchiseId(franchiseId);

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.addSubsidiaryToFranchise(request))
                .expectErrorSatisfies(throwable ->
                        assertTrue(throwable instanceof NotExistFranchiseException))
                .verify();

        verify(subsidiaryRepository, never()).save(any());
    }

    @Test
    void addSubsidiaryToFranchise_shouldPropagateRepositoryError() {
        // Arrange
        Long franchiseId = 3L;
        NewSubsidiaryRequestDTO request = new NewSubsidiaryRequestDTO();
        request.setFranchiseId(franchiseId);

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(service.addSubsidiaryToFranchise(request))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
                .verify();

        verify(subsidiaryRepository, never()).save(any());
    }
}