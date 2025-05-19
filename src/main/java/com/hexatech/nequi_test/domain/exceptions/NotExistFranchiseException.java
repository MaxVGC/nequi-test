package com.hexatech.nequi_test.domain.exceptions;

public class NotExistFranchiseException extends NotFoundElementException {

    private static final long serialVersionUID = 1L;

    public NotExistFranchiseException(String message) {
        super(message);
    }

    public NotExistFranchiseException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
