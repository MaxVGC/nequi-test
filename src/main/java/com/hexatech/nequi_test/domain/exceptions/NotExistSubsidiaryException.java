package com.hexatech.nequi_test.domain.exceptions;

public class NotExistSubsidiaryException extends NotFoundElementException {

    private static final long serialVersionUID = 1L;

    public NotExistSubsidiaryException(String message) {
        super(message);
    }

    public NotExistSubsidiaryException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
