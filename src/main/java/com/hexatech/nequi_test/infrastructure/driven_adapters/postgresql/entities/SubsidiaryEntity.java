package com.hexatech.nequi_test.infrastructure.driven_adapters.postgresql.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data
@Entity(name = "subsidiaries")
public class SubsidiaryEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "subsidiary", fetch = FetchType.EAGER)
    private List<ProductEntity> products;

    @ManyToOne
    @JoinColumn(name = "franchise_id", nullable = false)
    private FranchiseEntity franchise;
}
