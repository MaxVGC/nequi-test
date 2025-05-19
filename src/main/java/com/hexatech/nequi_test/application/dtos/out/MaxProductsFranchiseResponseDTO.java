package com.hexatech.nequi_test.application.dtos.out;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaxProductsFranchiseResponseDTO {
    private String franchiseName;
    private List<MaxStockProductBySubsidiaryDTO> topProductsBySubsidiary;
}
