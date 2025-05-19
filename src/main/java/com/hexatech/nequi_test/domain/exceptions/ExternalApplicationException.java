package com.hexatech.nequi_test.domain.exceptions;

public class ExternalApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExternalApplicationException(String message) {
        super(message);
    }

    public ExternalApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalApplicationException(Throwable cause) {
        super(cause);
    }
    
}
