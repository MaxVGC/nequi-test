package com.hexatech.nequi_test.application.services.impl;

import java.util.Comparator;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewFranchiseRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchFranchiseNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.FranchiseDTO;
import com.hexatech.nequi_test.application.dtos.out.MaxProductsFranchiseResponseDTO;
import com.hexatech.nequi_test.application.mappers.RequestMapper;
import com.hexatech.nequi_test.application.mappers.ResponseMapper;
import com.hexatech.nequi_test.application.ports.IFranchiseRepository;
import com.hexatech.nequi_test.application.services.IFranchiseService;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.exceptions.NotExistFranchiseException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseServiceImpl implements IFranchiseService {

    private final IFranchiseRepository franchiseRepository;
    private final RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
    private final ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);

    @Override
    public Mono<Void> addFranchise(NewFranchiseRequestDTO request) {
        return franchiseRepository.save(requestMapper.toFranchise(request))
                .doOnSubscribe(subscription -> log.debug("Adding new franchise: {}", request))
                .doOnSuccess(franchise -> log.info("Franchise added successfully: {}", franchise))
                .doOnError(e -> log.error("Error adding franchise: {}", e.getMessage()))
                .then()
                .onErrorMap(e -> {
                    log.error("Error in addFranchise: {}", e.getMessage());
                    return new RuntimeException("Failed to add franchise", e);
                });
    }

    @Override
    public Mono<Void> patchFranchiseName(PatchFranchiseNameRequestDTO request) {
        return franchiseRepository.findById(request.getId())
                .switchIfEmpty(Mono.error(new NotExistFranchiseException("message.error.franchise.not.exist")))
                .doOnSubscribe(subscription -> log.debug("Searching for franchise with ID {}", request.getId()))
                .doOnNext(franchise -> log.debug("Found franchise: {}", franchise.getName()))
                .flatMap(franchise -> {
                    franchise.setName(request.getNewName());
                    return franchiseRepository.save(franchise);
                })
                .doOnSuccess(updatedFranchise -> log.info("Franchise name updated successfully: {}", updatedFranchise))
                .doOnError(e -> log.error("Error updating franchise name: {}", e.getMessage()))
                .then();

    }

    @Override
    public Mono<MaxProductsFranchiseResponseDTO> getTopProductsByFranchise(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(
                        Mono.error(new NotExistFranchiseException("message.error.franchise.not.exist")))
                .doOnSubscribe(subscription -> log.debug("Searching for franchise with ID {}", franchiseId))
                .doOnNext(franchise -> log.debug("Found franchise: {}", franchise.getName()))
                .onErrorMap(e -> {
                    log.error("Error finding franchise with ID {}: {}", franchiseId, e.getMessage());
                    return e;
                })
                .flatMap(franchise -> Flux.fromIterable(franchise.getSubsidiaries())
                        .flatMap(subsidiary -> {
                            log.debug("Processing subsidiary: {}", subsidiary.getName());
                            Product topProduct = subsidiary.getProducts().stream()
                                    .max(Comparator.comparingInt(Product::getStock))
                                    .orElse(null);
                            return topProduct != null
                                    ? Mono.just(responseMapper.toMaxStockProductBySubsidiaryDTO(subsidiary, topProduct))
                                    : Mono.empty();
                        })
                        .collectList()
                        .map(topProductsList -> {
                            MaxProductsFranchiseResponseDTO response = new MaxProductsFranchiseResponseDTO();
                            response.setFranchiseName(franchise.getName());
                            response.setTopProductsBySubsidiary(topProductsList);
                            return response;
                        }));
    }

    @Override
    public Mono<PaginationResult<FranchiseDTO>> getAllPaginated(PaginationParameters paginationParameters) {
        return Mono.fromCallable(() -> {
            Page<Franchise> page = franchiseRepository.findAll(paginationParameters);
            List<FranchiseDTO> content = page.map(responseMapper::toFranchiseDTO).getContent();

            return new PaginationResult.Builder<FranchiseDTO>()
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .data(content)
                    .build();
        })
                .onErrorMap(e -> new RuntimeException("Error while retrieving paginated franchises", e))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
