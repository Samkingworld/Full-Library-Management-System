package com.groupproject.libraryManagementSystem.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidInputException(MethodArgumentNotValidException ex){
        Map<String, String> errorDetails = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errorDetails.put(error.getField(), error.getDefaultMessage())
        );
        return errorDetails;
    }
}
