package com.hexatech.nequi_test.application.mappers;

import org.mapstruct.Mapper;

import com.hexatech.nequi_test.application.dtos.out.FranchiseDTO;
import com.hexatech.nequi_test.application.dtos.out.MaxStockProductBySubsidiaryDTO;
import com.hexatech.nequi_test.application.dtos.out.ProductDTO;
import com.hexatech.nequi_test.application.dtos.out.SubsidiaryDTO;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.domain.models.Subsidiary;

@Mapper
public interface ResponseMapper {
    MaxStockProductBySubsidiaryDTO toMaxStockProductBySubsidiaryDTO(Subsidiary subsidiary, Product product);

    ProductDTO toProductDTO(Product product);

    FranchiseDTO toFranchiseDTO(Franchise franchise);

    SubsidiaryDTO toSubsidiaryDTO(Subsidiary subsidiary);
}
