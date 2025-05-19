package com.hexatech.nequi_test.application.dtos.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaxStockProductBySubsidiaryDTO {
    private SubsidiaryDTO subsidiary;
    private ProductDTO product;
}
