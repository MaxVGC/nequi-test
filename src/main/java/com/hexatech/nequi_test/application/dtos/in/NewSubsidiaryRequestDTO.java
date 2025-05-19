package com.hexatech.nequi_test.application.dtos.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewSubsidiaryRequestDTO {
    @NotBlank(message = "{message.validation.subsidiary.id.notblank}")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "{message.validation.subsidiary.name.pattern}")
    @Size(min = 1, max = 50, message = "{message.validation.subsidiary.name.size}")
    String name;

    @NotNull(message = "{message.validation.subsidiary.franchiseId.notblank}")
    Long franchiseId;
}
