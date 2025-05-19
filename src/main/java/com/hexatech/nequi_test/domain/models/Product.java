package com.hexatech.nequi_test.domain.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private Long id;
    private Long subsidiaryId;
    private String name;
    private Integer stock;
}
