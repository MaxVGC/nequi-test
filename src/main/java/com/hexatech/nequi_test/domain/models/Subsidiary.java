package com.hexatech.nequi_test.domain.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Subsidiary implements Serializable {
    private Long id;
    private Long franchiseId;
    private String name;
    private List<Product> products;
}
