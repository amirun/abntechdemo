package com.amirun.abdtechdmeonosql.exception;

/**
 * This exception is typically used to indicate that a requested recipe could not be found.
 */
public class RecipeNotFoundException extends Exception {
    public RecipeNotFoundException(String message) {
        super(message);
    }
}
