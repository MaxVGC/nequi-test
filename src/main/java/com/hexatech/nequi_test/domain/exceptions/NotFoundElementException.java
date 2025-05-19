package com.hexatech.nequi_test.domain.exceptions;

public class NotFoundElementException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotFoundElementException(String message) {
        super(message);
    }

    public NotFoundElementException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
