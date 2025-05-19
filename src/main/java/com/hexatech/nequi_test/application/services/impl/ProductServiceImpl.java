package com.hexatech.nequi_test.application.services.impl;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hexatech.nequi_test.application.dtos.handlers.PaginationParameters;
import com.hexatech.nequi_test.application.dtos.handlers.PaginationResult;
import com.hexatech.nequi_test.application.dtos.in.NewProductRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchProductNameRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.PatchStockRequestDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;
import com.hexatech.nequi_test.application.mappers.RequestMapper;
import com.hexatech.nequi_test.application.mappers.ResponseMapper;
import com.hexatech.nequi_test.application.ports.IProductRepository;
import com.hexatech.nequi_test.application.ports.ISubsidiaryRepository;
import com.hexatech.nequi_test.application.services.IProductService;
import com.hexatech.nequi_test.domain.exceptions.ExternalApplicationException;
import com.hexatech.nequi_test.domain.exceptions.NotExistProductException;
import com.hexatech.nequi_test.domain.exceptions.NotExistSubsidiaryException;
import com.hexatech.nequi_test.domain.models.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ISubsidiaryRepository subsidiaryRepository;

    private final RequestMapper EntityMapper = Mappers.getMapper(RequestMapper.class);
    private final ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);

    public Mono<Void> addProductToSubsidiary(NewProductRequestDTO request) {
        return subsidiaryRepository.findById(request.subsidiaryId())
                .switchIfEmpty(Mono.error(new NotExistSubsidiaryException("message.error.product.not.exist")))
                .doOnSubscribe(
                        subscription -> log.debug("Searching for subsidiary with ID {}", request.subsidiaryId()))
                .doOnNext(subsidiary -> log.debug("Found subsidiary: {}", subsidiary.getName()))
                .onErrorMap(e -> {
                    log.error("Error finding subsidiary with ID {}: {}", request.subsidiaryId(), e.getMessage());
                    return e;
                })
                .flatMap(subsidiary -> {
                    Product product = EntityMapper.toProduct(request);
                    return productRepository.save(product).then();
                })
                .doOnSuccess(aVoid -> log.info("Product added to subsidiary with ID {}", request.subsidiaryId()))
                .doOnError(e -> log.error("Error adding product to subsidiary: {}", e.getMessage()));
    }

    @Override
    public Mono<Void> patchProductName(PatchProductNameRequestDTO request) {
        return productRepository.findById(request.getId())
                .switchIfEmpty(Mono.error(new NotExistProductException("message.error.product.not.exist")))
                .doOnSubscribe(subscription -> log.debug("Searching for product with ID {}", request.getId()))
                .doOnNext(product -> log.debug("Found product: {}", product.getName()))
                .onErrorMap(e -> {
                    log.error("Error finding product with ID {}: {}", request.getId(), e.getMessage());
                    return e;
                })
                .flatMap(product -> {
                    product.setName(request.getNewName());
                    return productRepository.save(product).then();
                })
                .doOnSuccess(aVoid -> log.info("Product name updated for ID {}", request.getId()))
                .doOnError(e -> log.error("Error updating product name: {}", e.getMessage()));
    }

    @Override
    public Mono<Void> removeProductFromSubsidiary(Long subsidiaryId, Long productId) {
        return subsidiaryRepository.findById(subsidiaryId)
                .switchIfEmpty(Mono.error(new NotExistSubsidiaryException("message.error.subsidiary.not.exist")))
                .doOnSubscribe(subscription -> log.debug("Searching for subsidiary with ID {}", subsidiaryId))
                .doOnNext(subsidiary -> log.debug("Found subsidiary: {}", subsidiary.getName()))
                .onErrorMap(e -> {
                    log.error("Error finding subsidiary with ID {}: {}", subsidiaryId, e.getMessage());
                    return e;
                })
                .flatMap(subsidiary -> {
                    Product productToRemove = subsidiary.getProducts().stream()
                            .filter(product -> product.getId().equals(productId))
                            .findFirst()
                            .orElse(null);
                    if (productToRemove != null) {
                        return productRepository.delete(productToRemove);
                    } else {
                        return Mono.error(new NotExistProductException("message.error.product.not.exist"));
                    }
                })
                .doOnSuccess(aVoid -> log.info("Product removed from subsidiary with ID {}", subsidiaryId))
                .doOnError(e -> log.error("Error removing product from subsidiary: {}", e.getMessage()));
    }

    @Override
    public Mono<Void> patchStock(PatchStockRequestDTO request) {
        return productRepository.findById(request.getId())
                .switchIfEmpty(Mono.error(new NotExistProductException("message.error.product.not.exist")))
                .doOnSubscribe(subscription -> log.debug("Searching for product with ID {}", request.getId()))
                .doOnNext(product -> log.debug("Found product: {}", product.getName()))
                .onErrorMap(e -> {
                    log.error("Error finding product with ID {}: {}", request.getId(), e.getMessage());
                    return e;
                })
                .flatMap(product -> {
                    product.setStock(request.getStock());
                    return productRepository.save(product).then();
                })
                .doOnSuccess(aVoid -> log.info("Product stock updated for ID {}", request.getId()))
                .doOnError(e -> log.error("Error updating product stock: {}", e.getMessage()))
                .onErrorMap(e -> {
                    log.error("Error in patchStock: {}", e.getMessage());
                    return new RuntimeException("Failed to update product stock", e);
                });
    }

    @Override
    public Mono<PaginationResult<ProductDTO>> getAllPaginated(PaginationParameters paginationParameters) {
        return Mono.fromCallable(() -> {
            Page<Product> page = productRepository.findAll(paginationParameters);
            List<ProductDTO> content = page.map(responseMapper::toProductDTO).getContent();

            return new PaginationResult.Builder<ProductDTO>()
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .data(content)
                    .build();
        })
                .onErrorMap(e -> new ExternalApplicationException("Error while retrieving paginated products", e))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
