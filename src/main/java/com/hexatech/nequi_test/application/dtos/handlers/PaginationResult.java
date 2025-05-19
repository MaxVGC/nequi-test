package com.hexatech.nequi_test.application.dtos.handlers;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationResult<T> {
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;
    private List<T> data;

    public PaginationResult() {
        this.currentPage = 1;
        this.pageSize = 10;
    }

    private PaginationResult(Builder<T> builder) {
        this.currentPage = builder.currentPage;
        this.pageSize = builder.pageSize;
        this.totalPages = builder.totalPages;
        this.totalElements = builder.totalElements;
        this.data = builder.data;
    }

    public static class Builder<T> {
        private Integer currentPage = 1;
        private Integer pageSize = 10;
        private Integer totalPages;
        private Long totalElements;
        private List<T> data;

        public Builder<T> currentPage(Integer currentPage) {
            this.currentPage = currentPage + 1;
            return this;
        }

        public Builder<T> pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder<T> totalElements(Long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public PaginationResult<T> build() {
            return new PaginationResult<>(this);
        }
    }

}
