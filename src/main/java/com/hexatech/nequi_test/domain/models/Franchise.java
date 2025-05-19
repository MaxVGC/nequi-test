package com.hexatech.nequi_test.domain.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Franchise implements Serializable {
    private Long id;
    private String name;
    private List<Subsidiary> subsidiaries;    
}
