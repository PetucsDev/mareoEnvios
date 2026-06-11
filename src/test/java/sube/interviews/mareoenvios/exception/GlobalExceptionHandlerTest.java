package sube.interviews.mareoenvios.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler — tests unitarios")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("ResourceNotFoundException debe retornar 404 con mensaje")
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Shipping not found with id: 99");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("Shipping not found with id: 99");
    }

    @Test
    @DisplayName("InvalidStateTransitionException debe retornar 422 con mensaje")
    void handleInvalidTransition_shouldReturn422() {
        InvalidStateTransitionException ex = new InvalidStateTransitionException("Cannot transition from DELIVERED to INITIAL");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidTransition(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(422);
        assertThat(response.getBody().getMessage()).isEqualTo("Cannot transition from DELIVERED to INITIAL");
    }

    @Test
    @DisplayName("BusinessException debe retornar 400 con mensaje")
    void handleBusiness_shouldReturn400() {
        BusinessException ex = new BusinessException("Either customerId or newCustomer must be provided");

        ResponseEntity<ErrorResponse> response = handler.handleBusiness(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Either customerId or newCustomer must be provided");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException debe retornar 400 con fieldErrors")
    void handleValidation_shouldReturn400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("shippingCreateRequest", "sendDate", "Send date is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFieldErrors()).containsEntry("sendDate", "Send date is required");
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException debe retornar 400 con mensaje descriptivo")
    void handleTypeMismatch_shouldReturn400WithDescriptiveMessage() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("INVALID_STATE");
        when(ex.getName()).thenReturn("state");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("INVALID_STATE").contains("state");
    }

    @Test
    @DisplayName("Exception genérica debe retornar 500")
    void handleGeneric_shouldReturn500() {
        Exception ex = new RuntimeException("Unexpected DB failure");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }

    @Test
    @DisplayName("ErrorResponse debe incluir timestamp no nulo")
    void errorResponse_shouldHaveTimestamp() {
        ResourceNotFoundException ex = new ResourceNotFoundException("not found");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);

        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
