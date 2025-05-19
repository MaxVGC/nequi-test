package com.hexatech.nequi_test.application.dtos.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchStockRequestDTO {
    @NotNull(message = "{message.validation.product.id.notnull}")
    private Long id;
    @Min(value = 0, message = "{message.validation.product.stock.min}")
    @NotNull(message = "{message.validation.product.stock.notnull}")
    private Integer stock;
}
