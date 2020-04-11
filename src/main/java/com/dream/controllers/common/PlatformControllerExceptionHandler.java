package com.dream.controllers.common;

import com.dream.exceptions.AuthorizationException;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.utils.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler that maps exceptions to HTTP error status codes 400+ or 500
 */

@Slf4j
@RestControllerAdvice
public class PlatformControllerExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthorizationException(AuthorizationException e) {
        log.info("Unauthorized, message: {}", e.getMessage());
        return new ErrorResponse(e.getErrorCode(), e.toString(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.info("Resource not found, message: {}", e.getMessage());
        return new ErrorResponse(e.getErrorCode(), e.toString(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    public ErrorResponse handleOperationNotPermittedException(OperationNotPermittedException e) {
        log.info("Operation is not permitted, message: {}", e.getMessage());
        return new ErrorResponse(e.getErrorCode(), e.toString(), e.getMessage());
    }

}
