package com.hexatech.nequi_test.application.dtos.out;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    private HttpStatus status;
    private int code;
    private String message;
}
