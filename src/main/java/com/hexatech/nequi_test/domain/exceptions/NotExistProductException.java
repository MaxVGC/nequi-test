package com.hexatech.nequi_test.domain.exceptions;

public class NotExistProductException extends NotFoundElementException {
    private static final long serialVersionUID = 1L;

   public NotExistProductException(String message) {
        super(message);
    }

    public NotExistProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
