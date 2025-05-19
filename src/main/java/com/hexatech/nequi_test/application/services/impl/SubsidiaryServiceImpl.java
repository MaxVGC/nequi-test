package com.hexatech.nequi_test.application.services.impl;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewSubsidiaryRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchSubsidiaryNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.SubsidiaryDTO;
import com.hexatech.nequi_test.application.mappers.RequestMapper;
import com.hexatech.nequi_test.application.mappers.ResponseMapper;
import com.hexatech.nequi_test.application.ports.IFranchiseRepository;
import com.hexatech.nequi_test.application.ports.ISubsidiaryRepository;
import com.hexatech.nequi_test.application.services.ISubsidiaryService;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.exceptions.NotExistFranchiseException;
import com.hexatech.nequi_test.domain.exceptions.NotExistSubsidiaryException;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Subsidiary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubsidiaryServiceImpl implements ISubsidiaryService {
        private final ISubsidiaryRepository subsidiaryRepository;
        private final IFranchiseRepository franchiseRepository;
        private final RequestMapper mapper = Mappers.getMapper(RequestMapper.class);
        private final ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);

        @Override
        public Mono<Void> patchSubsidiaryName(PatchSubsidiaryNameRequestDTO request) {
                return subsidiaryRepository.findById(request.getId())
                                .switchIfEmpty(
                                                Mono.error(new NotExistSubsidiaryException(
                                                                "Subsidiary with ID " + request.getId()
                                                                                + " does not exist.")))
                                .doOnSubscribe(subscription -> log.debug("Searching for subsidiary with ID {}",
                                                request.getId()))
                                .doOnNext(subsidiary -> log.debug("Found subsidiary: {}", subsidiary.getName()))
                                .flatMap(subsidiary -> {
                                        subsidiary.setName(request.getNewName());
                                        return subsidiaryRepository.save(subsidiary).then();
                                })
                                .doOnSuccess(aVoid -> log.info("Subsidiary name updated successfully"))
                                .doOnError(e -> log.error("Error updating subsidiary name: {}", e.getMessage()))
                                .onErrorMap(e -> {
                                        log.error("Error in patchSubsidiaryName: {}", e.getMessage());
                                        return new RuntimeException("Failed to update subsidiary name", e);
                                });
        }

        @Override
        public Mono<Void> addSubsidiaryToFranchise(NewSubsidiaryRequestDTO request) {
                return franchiseRepository.findById(request.getFranchiseId())
                                .switchIfEmpty(Mono.error(new NotExistFranchiseException(
                                                "Franchise with ID " + request.getFranchiseId() + " does not exist.")))
                                .onErrorResume(Mono::error)
                                .doOnSubscribe(
                                                subscription -> log.debug("Searching for franchise with ID {}",
                                                                request.getFranchiseId()))
                                .doOnNext(franchise -> log.debug("Found franchise: {}", franchise.getName()))
                                .flatMap(franchise -> {
                                        Subsidiary subsidiary = mapper.toSubsidiary(request);
                                        return subsidiaryRepository.save(subsidiary).then();
                                })
                                .doOnSuccess(aVoid -> log.info("Subsidiary added to franchise with ID {}",
                                                request.getFranchiseId()))
                                .doOnError(e -> log.error("Error adding subsidiary to franchise: {}", e.getMessage()));
        }

        @Override
        public Mono<PaginationResult<SubsidiaryDTO>> getAllPaginated(PaginationParameters paginationParameters) {
                return Mono.fromCallable(() -> {
                        Page<Subsidiary> page = subsidiaryRepository.findAll(paginationParameters);
                        List<SubsidiaryDTO> content = page.map(responseMapper::toSubsidiaryDTO).getContent();

                        return new PaginationResult.Builder<SubsidiaryDTO>()
                                        .currentPage(page.getNumber())
                                        .pageSize(page.getSize())
                                        .totalPages(page.getTotalPages())
                                        .totalElements(page.getTotalElements())
                                        .data(content)
                                        .build();
                })
                                .onErrorMap(e -> new ExternalApplicationException(
                                                "Error while retrieving paginated subsidiaries", e))
                                .subscribeOn(Schedulers.boundedElastic());
        }

}
