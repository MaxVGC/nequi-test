package com.hexatech.nequi_test.application.services;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewProductRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchProductNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchStockRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;

import reactor.core.publisher.Mono;

public interface IProductService {

    Mono<Void> addProductToSubsidiary(NewProductRequestDTO request);

    Mono<Void> patchProductName(PatchProductNameRequestDTO request);

    Mono<Void> removeProductFromSubsidiary(Long subsidiaryId, Long productId);

    Mono<Void> patchStock(PatchStockRequestDTO request);

    public Mono<PaginationResult<ProductDTO>> getAllPaginated(PaginationParameters paginationParameters);

}
