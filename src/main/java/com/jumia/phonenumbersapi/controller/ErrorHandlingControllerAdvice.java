package com.jumia.phonenumbersapi.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@SuppressWarnings("rawtypes")
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
class ErrorHandlingControllerAdvice {

    private static final String ERROR_KEY = "error";
    private static final String MISSING_ONE_OR_MORE_FROM_REQUEST_PARAMETERS = "Missing one or more from request parameters, {0}";
    private static final String REQUESTED_PARAMETERS_IN_WRONG_FORMAT = "Failed to parse the request parameters as they are in wrong format.";
    private static final String UNEXPECTED_EXCEPTION = "something went wrong.";
    private static final String VALIDATION_FAILED = "Validation failed, {0}.";
    private static final String NOT_FOUND = "Entity not found, {0}.";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity onEntityNotFoundException(final EntityNotFoundException e)
    {
        log.warn(NOT_FOUND, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AbstractMap.SimpleEntry<>(ERROR_KEY, e.getMessage()));
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity onMissingServletRequestParameterException(final MissingServletRequestParameterException e)
    {
        log.warn(MISSING_ONE_OR_MORE_FROM_REQUEST_PARAMETERS, e);
        return ResponseEntity.badRequest().body(new AbstractMap.SimpleEntry<>(ERROR_KEY, e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity onMissingServletRequestParameterException(final MethodArgumentNotValidException e)
    {
        log.warn(MISSING_ONE_OR_MORE_FROM_REQUEST_PARAMETERS, e);
        final List<String> errors = new ArrayList<>(50);
        e.getBindingResult().getAllErrors().forEach(error ->
                errors.add(((FieldError) error).getField() + " " + error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(new AbstractMap.SimpleEntry<>(ERROR_KEY, errors));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        final String errorMessage = "Invalid " + e.getName();
        return ResponseEntity.badRequest().body(new AbstractMap.SimpleEntry<>(ERROR_KEY, errorMessage));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onIllegalArgumentException(final IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        final String errorMessage = e.getMessage();
        return ResponseEntity.badRequest().body(new AbstractMap.SimpleEntry<>(ERROR_KEY, errorMessage));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onInvalidBodyParameter(final InvalidFormatException e) {
        log.error(e.getMessage(), e);
        final String error = "Invalid " + e.getPath().get(0).getFieldName();
        return ResponseEntity.badRequest().body(new AbstractMap.SimpleEntry<>(ERROR_KEY, error));
    }
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onInvalidDataAccessApiUsageExceptionException(final InvalidDataAccessApiUsageException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new AbstractMap.SimpleEntry<>(ERROR_KEY, e.getMessage()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new AbstractMap.SimpleEntry<>(ERROR_KEY, REQUESTED_PARAMETERS_IN_WRONG_FORMAT));
    }
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onException(final ValidationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new AbstractMap.SimpleEntry<>(ERROR_KEY, format(VALIDATION_FAILED, e.getMessage())));
    }
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onException(final BindException e) {
        log.error(e.getMessage(), e);
        String errorMessage;
        if(e.getMessage().contains("#validator ")) {
            errorMessage = e.getMessage().split("#validator")[1];
        } else {
            errorMessage =  format(VALIDATION_FAILED, e.getMessage());
        }

        return ResponseEntity.badRequest()
                .body(new AbstractMap.SimpleEntry<>(ERROR_KEY, errorMessage));
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<AbstractMap.SimpleEntry<String, String>> onException(final Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new AbstractMap.SimpleEntry<>(ERROR_KEY, UNEXPECTED_EXCEPTION));
    }


}
