package astoppello.wallet.controller;

import astoppello.wallet.domain.Account;
import astoppello.wallet.dto.ErrorDto;
import astoppello.wallet.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MvcExceptionHanlderTest {

    MvcExceptionHanlder handler = new MvcExceptionHanlder();

    @Test
    void validationErrorHandler() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("name");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("must not be blank");

        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("balance");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("must not be null");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation1, violation2));

        ResponseEntity<List<ErrorDto>> response = handler.validationErrorHandler(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting("message")
                .containsExactlyInAnyOrder("name : must not be blank", "balance : must not be null");
    }

    @Test
    void notFoundErrorHandler() {
        ResponseEntity<ErrorDto> response = handler.notFoundErrorHandler(new NotFoundException(Account.class, "Revolut"));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().extracting("message").isEqualTo("Account with name Revolut not found");

        UUID uuid = UUID.randomUUID();
        ResponseEntity<ErrorDto> response1 = handler.notFoundErrorHandler(new NotFoundException(Account.class, uuid));
        assertThat(response1.getBody().getMessage()).isEqualTo("Account with id " + uuid + " not found");
    }

    @Test
    void handleValidationExceptions() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("account", "name", "must not be blank"),
                new FieldError("account", "currency", "must not be null")
        ));

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("name", "must not be blank")
                .containsEntry("currency", "must not be null");
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute statement", new RuntimeException("duplicate key value violates unique constraint"));

        ResponseEntity<ErrorDto> response = handler.handleDataIntegrityViolation(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("duplicate key value violates unique constraint");
    }
}