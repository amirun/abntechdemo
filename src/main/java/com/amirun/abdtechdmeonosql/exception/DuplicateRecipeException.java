package com.amirun.abdtechdmeonosql.exception;

/**
 * Exception thrown when attempting to create a recipe with a duplicate name.
 * This exception is typically used to indicate that a recipe with the same name already exists.
 */
public class DuplicateRecipeException extends Exception{
    public DuplicateRecipeException(String message) {
        super(message);
    }
}
