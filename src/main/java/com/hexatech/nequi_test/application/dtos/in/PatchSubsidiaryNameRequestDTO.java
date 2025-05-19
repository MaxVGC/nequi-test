package com.hexatech.nequi_test.application.dtos.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchSubsidiaryNameRequestDTO {
    @NotNull(message = "{message.validation.subsidiary.id.notnull}")
    private Long id;
    @NotBlank(message = "{message.validation.subsidiary.id.notblank}")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "{message.validation.subsidiary.name.pattern}")
    @Size(min = 1, max = 50, message = "{message.validation.subsidiary.name.size}")
    private String newName;
}
