package com.amirun.abdtechdmeonosql.utils;

import com.amirun.abdtechdmeonosql.dto.ErrorDTO;

public class RecipeUtils {
    private RecipeUtils(){

    }

    /**
     * Constructs an {@link ErrorDTO} object based on the provided error message and exception.
     *
     * @param message The description or summary of the error.
     * @param exceptionMessage Additional error details.
     * @return An {@link ErrorDTO} object containing the error message and details.
     */
    public static ErrorDTO getErrorDTO(String message, String exceptionMessage) {
       return ErrorDTO.builder().description(message).errorDetails(exceptionMessage).build();
    }
}
