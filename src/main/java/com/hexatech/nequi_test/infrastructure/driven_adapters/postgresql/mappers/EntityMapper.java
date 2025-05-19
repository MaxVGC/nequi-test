package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hexatech.nequi_test.domain.models.Franchise;
import com.hexatech.nequi_test.domain.models.Product;
import com.hexatech.nequi_test.domain.models.Subsidiary;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.FranchiseEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.ProductEntity;
import com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities.SubsidiaryEntity;

@Mapper
public interface EntityMapper {
    FranchiseEntity toFranchiseEntity(Franchise franchise);

    Franchise toFranchise(FranchiseEntity franchiseEntity);

    @Mapping(target = "subsidiary.id", source = "subsidiaryId")
    ProductEntity toProductEntity(Product product);

    @Mapping(target = "subsidiaryId", source = "subsidiary.id")
    Product toProduct(ProductEntity productEntity);

    @Mapping(target = "franchise.id", source = "franchiseId")
    SubsidiaryEntity toSubsidiayEntity(Subsidiary subsidiay);

    @Mapping(target = "franchiseId", source = "franchise.id")
    Subsidiary toSubsidiary(SubsidiaryEntity subsidiayEntity);
}
