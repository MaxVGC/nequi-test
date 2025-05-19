package com.hexatech.nequi_test.application.handlers;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;

import com.hexatech.nequi_test.application.dtos.out.ErrorDTO;
import com.hexatech.nequi_test.domain.exceptions.NotFoundElementException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    private BindingValuesHandler mapToBindingValues(ObjectError error) {
        String field = ((DefaultMessageSourceResolvable) error.getArguments()[0]).getCodes()[1];
        String errorMessage = error.getDefaultMessage();
        return BindingValuesHandler.builder()
                .field(field)
                .message(errorMessage)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class,WebExchangeBindException.class})
    public Mono<ResponseEntity<ErrorDTO>> handleHandlerMethodValidation(MethodArgumentNotValidException ex,
            Locale locale) {
        BindingValuesHandler errorMap = mapToBindingValues(ex.getAllErrors().getFirst());
        ErrorDTO error = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(messageSource.getMessage(errorMap.getMessage(), null, errorMap.getMessage(),
                        Locale.getDefault()))
                .build();
        return Mono.just(new ResponseEntity<>(error, HttpStatus.BAD_REQUEST));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorDTO>> handleException(Exception ex) {
        log.error("Exception : {}", ex);
        ErrorDTO error = ErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(messageSource.getMessage("error.internal.server", null, "Internal Server Error",
                        Locale.getDefault()))
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundElementException.class)
    public Mono<ResponseEntity<ErrorDTO>> handleNotFoundElementException(NotFoundElementException ex, Locale locale) {
        log.error("NotFoundElementException : {}", ex.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(messageSource.getMessage(ex.getMessage(), null, "No message", locale))
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ NoResourceFoundException.class, MethodNotAllowedException.class })
    public ResponseEntity<Void> handleNotFoundExceptions(RuntimeException ex, Locale locale) {
        log.error(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

}
