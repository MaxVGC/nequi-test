package com.hexatech.nequi_test.application.handlers;
import com.hexatech.nequi_test.application.dtos.out.ErrorDTO;
import com.hexatech.nequi_test.domain.exceptions.NotFoundElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;





class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Test exception");
        when(messageSource.getMessage(eq("error.internal.server"), any(), any(), any(Locale.class)))
                .thenReturn("Internal Server Error");

        Mono<ResponseEntity<ErrorDTO>> responseMono = globalExceptionHandler.handleException(ex);
        ResponseEntity<ErrorDTO> response = responseMono.block();

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundElementException_shouldReturnBadRequest() {
        NotFoundElementException ex = new NotFoundElementException("not.found");
        when(messageSource.getMessage(eq("not.found"), any(), any(), any(Locale.class)))
                .thenReturn("Element not found");

        Mono<ResponseEntity<ErrorDTO>> responseMono = globalExceptionHandler.handleNotFoundElementException(ex, Locale.getDefault());
        ResponseEntity<ErrorDTO> response = responseMono.block();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Element not found", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundExceptions_shouldReturnNotFound() {
        NoResourceFoundException ex = new NoResourceFoundException("xd");
        ResponseEntity<Void> response = globalExceptionHandler.handleNotFoundExceptions(ex, Locale.getDefault());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        MethodNotAllowedException ex2 = new MethodNotAllowedException("GET", Collections.emptyList());
        ResponseEntity<Void> response2 = globalExceptionHandler.handleNotFoundExceptions(ex2, Locale.getDefault());
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }
}