package com.hexatech.nequi_test.application.dtos.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewFranchiseRequestDTO(
        @NotBlank(message = "{message.validation.product.id.notblank}")
        @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "{message.validation.product.name.pattern}")
        @Size(min = 1, max = 50, message = "{message.validation.product.name.size}")
        String name) {

}
