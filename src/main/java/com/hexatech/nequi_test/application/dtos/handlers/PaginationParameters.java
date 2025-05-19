package com.hexatech.nequi_test.application.dtos.handlers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationParameters {
    @Pattern(regexp = "^[a-zA-Z]*$", message = "{message.validation.pagination.sortby.invalid}")
    private String sortBy = "id";
    @Min(value = 1, message = "{message.validation.pagination.page.min}")
    private Integer page = 1;

    @Min(value = 1, message = "{message.validation.pagination.pagesize.min}")
    @Max(value = 50, message = "{message.validation.pagination.pagesize.max}")
    private Integer pageSize = 10;

    private Boolean sortAscending = false;

    public PaginationParameters() {
        this.page = 1;
        this.pageSize = 10;
        this.sortAscending = false;
    }

    public PaginationParameters(PaginationParameters paginationParameters) {
        this.sortBy = paginationParameters.getSortBy();
        this.sortAscending = paginationParameters.getSortAscending();
        this.page = paginationParameters.getPage();
        this.pageSize = paginationParameters.getPageSize();
    }
}
