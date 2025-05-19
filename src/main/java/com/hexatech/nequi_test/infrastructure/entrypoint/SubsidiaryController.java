package com.hexatech.nequi_test.infrastructure.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewSubsidiaryRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchSubsidiaryNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;
import com.hexatech.nequi_test.application.dtos.out.SubsidiaryDTO;
import com.hexatech.nequi_test.application.services.ISubsidiaryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/subsidiaries")
@RequiredArgsConstructor
public class SubsidiaryController {

    private final ISubsidiaryService subsidiaryService;

    @PatchMapping("/name")
    public ResponseEntity<Mono<Void>> patchSubsidiaryName(@RequestBody @Valid PatchSubsidiaryNameRequestDTO request) {
        return new ResponseEntity<>(subsidiaryService.patchSubsidiaryName(request), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Mono<Void>> addSubsidiaryToFranchise(@RequestBody @Valid NewSubsidiaryRequestDTO request) {
        return new ResponseEntity<>(subsidiaryService.addSubsidiaryToFranchise(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Mono<PaginationResult<SubsidiaryDTO>>> getAllProducts(@Valid PaginationParameters params) {
        return new ResponseEntity<>(subsidiaryService.getAllPaginated(params), HttpStatus.OK);
    }

}
