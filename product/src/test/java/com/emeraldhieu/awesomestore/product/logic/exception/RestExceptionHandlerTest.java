package com.emeraldhieu.awesomestore.product.logic.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {

    private RestExceptionHandler restExceptionHandler;
    private MessageSource messageSource;

    @BeforeEach
    public void setUp() {
        messageSource = mock(MessageSource.class);
        restExceptionHandler = new RestExceptionHandler(messageSource);
    }

    @Test
    void givenMessageSource_whenHandleInvalidSortOrderException_thenReturnResponseEntity() {
        // GIVEN
        String expectedErrorMessage = "anything";
        when(messageSource.getMessage("invalidSortOrder", null, null)).thenReturn(expectedErrorMessage);

        // WHEN
        ResponseEntity<ProblemDetail> responseEntity = restExceptionHandler.handleInvalidSortOrder();

        // THEN
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        assertEquals(RestExceptionHandler.PROBLEM_TYPE_URI, responseEntity.getBody().getType());
        assertEquals(expectedErrorMessage, responseEntity.getBody().getDetail());
    }

    @Test
    void givenExceptionAndMessageSource_whenHandleMethodArgumentNotValid_thenReturnResponseEntity() {
        // GIVEN
        List<FieldError> expectedFieldErrors = List.of(new FieldError("objectA", "fieldB", "is invalid"));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(expectedFieldErrors);

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        String expectedErrorMessage = "anything";
        when(messageSource.getMessage("invalidRequestBodyArgument", null, null)).thenReturn(expectedErrorMessage);

        // WHEN
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleMethodArgumentNotValid(exception, null, HttpStatus.UNPROCESSABLE_ENTITY, null);

        // THEN
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ProblemDetail problemDetail = (ProblemDetail) responseEntity.getBody();
        assertEquals(RestExceptionHandler.PROBLEM_TYPE_URI, problemDetail.getType());
        assertEquals(expectedErrorMessage, problemDetail.getDetail());
        List<String> actualFieldErrors = (List<String>) problemDetail.getProperties().get(RestExceptionHandler.FIELD_ERRORS);
        assertEquals("fieldB is invalid", actualFieldErrors.get(0));
    }

    @Test
    void givenExceptionAndMessageSource_whenHandleHttpMessageNotReadableForInvalidField_thenReturnResponseEntity() {
        // GIVEN
        JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
        String expectedFieldName = "field42";
        when(reference.getFieldName()).thenReturn(expectedFieldName);

        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        String expectedErrorMessage = "JSON is invalid";
        when(messageSource.getMessage("invalidJson", null, null)).thenReturn(expectedErrorMessage);

        // WHEN
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleHttpMessageNotReadable(exception, null, HttpStatus.UNPROCESSABLE_ENTITY, null);

        // THEN
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ProblemDetail problemDetail = (ProblemDetail) responseEntity.getBody();
        assertEquals(RestExceptionHandler.PROBLEM_TYPE_URI, problemDetail.getType());
        assertEquals(expectedErrorMessage, problemDetail.getDetail());
    }

    @Test
    void givenExceptionAndMessageSource_whenHandleHttpMessageNotReadableForInvalidJson_thenReturnResponseEntity() {
        // GIVEN
        JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
        String expectedFieldName = "field42";
        when(reference.getFieldName()).thenReturn(expectedFieldName);

        JsonMappingException cause = mock(JsonMappingException.class);
        when(cause.getPath()).thenReturn(List.of(reference));

        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(cause);

        String expectedErrorMessage = expectedFieldName + " is invalid";
        when(messageSource.getMessage("invalidField", new Object[]{expectedFieldName}, null)).thenReturn(expectedErrorMessage);

        // WHEN
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleHttpMessageNotReadable(exception, null, HttpStatus.UNPROCESSABLE_ENTITY, null);

        // THEN
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ProblemDetail problemDetail = (ProblemDetail) responseEntity.getBody();
        assertEquals(RestExceptionHandler.PROBLEM_TYPE_URI, problemDetail.getType());
        assertEquals(expectedErrorMessage, problemDetail.getDetail());
    }

    @Test
    void givenExceptionAndMessageSource_whenHandleProductNotFound_thenReturnResponseEntity() {
        // GIVEN
        String productId = "product42";
        String expectedErrorMessage = "anything";
        when(messageSource.getMessage("productNotFound", new Object[]{productId}, null)).thenReturn(expectedErrorMessage);

        ProductNotFoundException exception = mock(ProductNotFoundException.class);
        when(exception.getProductId()).thenReturn(productId);

        // WHEN
        ResponseEntity<ProblemDetail> responseEntity = restExceptionHandler.handleProductNotFound(exception);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(RestExceptionHandler.PROBLEM_TYPE_URI, responseEntity.getBody().getType());
        assertEquals(expectedErrorMessage, responseEntity.getBody().getDetail());
    }
}