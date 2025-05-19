package com.hexatech.nequi_test.application.dtos.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchProductNameRequestDTO {
    @NotNull(message = "{message.validation.product.id.notnull}")
    private Long id;
    @NotBlank(message = "{message.validation.product.id.notblank}")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "{message.validation.product.name.pattern}")
    @Size(min = 1, max = 50, message = "{message.validation.product.name.size}")
    private String newName;
}
