package com.amirun.abdtechdmeonosql.controller;

import com.amirun.abdtechdmeonosql.constants.MessageConstants;
import com.amirun.abdtechdmeonosql.dto.ErrorDTO;
import com.amirun.abdtechdmeonosql.exception.DuplicateRecipeException;
import com.amirun.abdtechdmeonosql.exception.InvalidFilterException;
import com.amirun.abdtechdmeonosql.exception.RecipeNotFoundException;
import com.amirun.abdtechdmeonosql.utils.RecipeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = RecipeController.class)
public class RecipeControllerAdvice {

    @ExceptionHandler(DuplicateRecipeException.class)
    public ResponseEntity<ErrorDTO> handleDuplicateRecipe(DuplicateRecipeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                RecipeUtils.getErrorDTO(MessageConstants.DUPLICATE_RECIPE, e.getMessage()));

    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundExceptions(RecipeNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                RecipeUtils.getErrorDTO(MessageConstants.RECIPE_NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidFilterException.class})
    public ResponseEntity<ErrorDTO> handleMalFormedRequestExceptions(Exception e) {
        return ResponseEntity.badRequest().body(
                RecipeUtils.getErrorDTO(MessageConstants.INVALID_INPUT, e.getMessage()));
    }


    /**
     * Exception handler method to handle MethodArgumentNotValidException, which occurs when there are validation errors
     * in request parameters or request body.
     * MethodArgumentNotValidException.getFieldError().getDefaultMessage() returns the default error message if specified in the actual object
     *
     * @param e The MethodArgumentNotValidException thrown when validation fails.
     * @return ResponseEntity containing an ErrorDTO with a bad request status code (400) and error message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleInvalidRequestExceptions(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(
                RecipeUtils.getErrorDTO(MessageConstants.INVALID_INPUT, e.getFieldError() == null ? e.getMessage() : e.getFieldError().getDefaultMessage()));
    }
}
