package com.amirun.abdtechdmeonosql.exception;

/**
 * This exception thrown when an invalid filter criteria is provided for recipe filtering.
 */
public class InvalidFilterException extends Exception{
    public InvalidFilterException(String message) {
        super(message);
    }
}
