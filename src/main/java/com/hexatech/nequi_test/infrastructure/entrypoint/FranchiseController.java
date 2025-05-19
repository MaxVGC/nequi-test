package com.hexatech.nequi_test.infrastructure.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewFranchiseRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchFranchiseNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.FranchiseDTO;
import com.hexatech.nequi_test.application.dtos.out.MaxProductsFranchiseResponseDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;
import com.hexatech.nequi_test.application.services.IFranchiseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/v1/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final IFranchiseService franchiseService;

    @PostMapping
    public ResponseEntity<Mono<Void>> addFranchise(@RequestBody @Valid NewFranchiseRequestDTO request) {
        return new ResponseEntity<>(franchiseService.addFranchise(request), HttpStatus.CREATED);
    }

    @PatchMapping("/name")
    public ResponseEntity<Mono<Void>> patchFranchiseName(@RequestBody @Valid PatchFranchiseNameRequestDTO request) {
        return new ResponseEntity<>(franchiseService.patchFranchiseName(request), HttpStatus.OK);
    }

    @GetMapping("/top-products-by-subsidiary")
    public ResponseEntity<Mono<MaxProductsFranchiseResponseDTO>> getTopProductsByFranchise(
            @RequestParam Long franchiseId) {
        return ResponseEntity.ok(franchiseService.getTopProductsByFranchise(franchiseId));
    }

    @GetMapping
    public ResponseEntity<Mono<PaginationResult<FranchiseDTO>>> getAllProducts(@Valid PaginationParameters params) {
        return new ResponseEntity<>(franchiseService.getAllPaginated(params), HttpStatus.OK);
    }

}
