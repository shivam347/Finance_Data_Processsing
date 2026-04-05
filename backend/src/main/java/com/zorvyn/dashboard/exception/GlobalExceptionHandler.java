package com.zorvyn.dashboard.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j // HELPS US TO WRITE LOGS ON THE CONSOLE, USE of logger
@RestControllerAdvice  // Listenes for exception thrown anywhere inside RestController and Service
class GlobalExceptionHandler{

    /* Private method which define the respnse error type so this method can be used internally by global exception,
    how to throw the error in consistent way  */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, List<String> details){
          ErrorResponse errorResponse = ErrorResponse.builder()
          .success(false)
          .error(message)
          .details(details)
          .timestamp(LocalDateTime.now())
          .build();
          return new ResponseEntity<>(errorResponse, status);
    }

    

    /* Method to handle Api Exception  */
    @ExceptionHandler(ApiException.class) // tells spring if any api exception happens use this method
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex){
        // First log into the server
        log.error("API Exception: {}", ex.getMessage());
        return buildErrorResponse(ex.getStatus(), ex.getMessage(), null);

    }

    /* Method to handle @Valid exception means if email is valid or not handle that type of exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException ex){
        List<String> details = ex.getBindingResult().getFieldErrors()
        .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        log.error("Validation Exception: {}", details);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }


    /* Method for AccessDenied Exception 
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex){
        // log into server
        log.error("Access Denied: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access Denied", null);
    }


    /* Method to handle Authentication Exceptions */
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex){
        // First log into the server
        log.error("Authentication Exception:{}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", null);
    }


    /* Method to handle all other any global Exception handler */
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex){
        log.error("Internal Server Error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }


}
