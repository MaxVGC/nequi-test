package com.hexatech.nequi_test.application.services;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewFranchiseRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchFranchiseNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.FranchiseDTO;
import com.hexatech.nequi_test.application.dtos.out.MaxProductsFranchiseResponseDTO;

import reactor.core.publisher.Mono;

public interface IFranchiseService {

    public Mono<Void> addFranchise(NewFranchiseRequestDTO request);

    public Mono<Void> patchFranchiseName(PatchFranchiseNameRequestDTO request);

    public Mono<MaxProductsFranchiseResponseDTO> getTopProductsByFranchise(Long franchiseId);

    public Mono<PaginationResult<FranchiseDTO>> getAllPaginated(PaginationParameters paginationParameters);

}
