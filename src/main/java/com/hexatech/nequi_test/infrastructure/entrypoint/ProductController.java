package com.hexatech.nequi_test.infrastructure.entrypoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewProductRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchProductNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;
import com.hexatech.nequi_test.application.services.IProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @PostMapping
    public ResponseEntity<Mono<Void>> addProduct(@RequestBody @Valid NewProductRequestDTO entity) {
        return new ResponseEntity<>(productService.addProductToSubsidiary(entity), HttpStatus.CREATED);
    }
    
    @PatchMapping("/name")
    public ResponseEntity<Mono<Void>> patchProductName(@RequestBody @Valid PatchProductNameRequestDTO entity) {
        return new ResponseEntity<>(productService.patchProductName(entity), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Mono<Void>> removeProductFromSubsidiary(Long subsidiaryId, Long productId) {
        return new ResponseEntity<>(productService.removeProductFromSubsidiary(subsidiaryId, productId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Mono<PaginationResult<ProductDTO>>> getAllProducts(@Valid PaginationParameters params) {
       return new ResponseEntity<>(productService.getAllPaginated(params), HttpStatus.OK);
    }
    

   
    
    
}
