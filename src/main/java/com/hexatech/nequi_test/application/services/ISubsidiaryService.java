package com.hexatech.nequi_test.application.services;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewSubsidiaryRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchSubsidiaryNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.SubsidiaryDTO;

import reactor.core.publisher.Mono;

public interface ISubsidiaryService {

    public Mono<Void> patchSubsidiaryName(PatchSubsidiaryNameRequestDTO request);

    public Mono<Void> addSubsidiaryToFranchise(NewSubsidiaryRequestDTO request);

    public Mono<PaginationResult<SubsidiaryDTO>> getAllPaginated(PaginationParameters paginationParameters);

}
