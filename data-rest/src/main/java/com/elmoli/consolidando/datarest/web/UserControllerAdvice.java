/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest.web;

import com.elmoli.consolidando.datarest.domain.UserNotFoundException;
import com.elmoli.consolidando.datarest.domain.UserPictureNotFoundException;
import com.elmoli.consolidando.datarest.storage.StorageReadingException;
import com.elmoli.consolidando.datarest.storage.StorageRenamingException;
import com.elmoli.consolidando.datarest.storage.StorageWritingException;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author joanr
 */
@ControllerAdvice
public class UserControllerAdvice extends ResponseEntityExceptionHandler
{

    @ExceptionHandler(
    {
        UserPictureNotFoundException.class, 
        UserNotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleUserPictureNotFound(UserPictureNotFoundException ex)
    {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return (ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail));
    }

    @ExceptionHandler(
            {
                StorageReadingException.class,
                StorageWritingException.class,
                StorageRenamingException.class
            })
    public ResponseEntity<ProblemDetail> handleStorageException(RuntimeException ex)
    {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return (ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail));
    }
    
    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<ProblemDetail> handleAuthenticationException(RuntimeException ex) {

        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }
    
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(RuntimeException ex) {

        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, 
                        HttpHeaders headers, 
                        HttpStatusCode status, 
                        WebRequest request)
    {

	ProblemDetail problemDetail = ex.getBody();
	
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error ->
        {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        problemDetail.setProperty("Validation Errors:", errors);
        
        return super.handleMethodArgumentNotValid(ex, headers, status, request);           
        
    }
}
