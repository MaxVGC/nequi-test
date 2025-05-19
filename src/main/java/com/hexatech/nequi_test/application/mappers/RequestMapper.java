package com.hexatech.nequi_test.application.mappers;

import org.mapstruct.Mapper;

import com.hexatech.nequi_test.application.dtos.in.NewFranchiseRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.NewProductRequestDTO;
import com.hexatech.nequi_test.application.dtos.in.NewSubsidiaryRequestDTO;
import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.domain.models.Subsidiary;

@Mapper
public interface RequestMapper {
    Franchise toFranchise(NewFranchiseRequestDTO request);

    Product toProduct(NewProductRequestDTO request);

    Subsidiary toSubsidiary(NewSubsidiaryRequestDTO request);
}
