package com.amirun.abdtechdmeonosql.ut.utils;

import com.amirun.abdtechdmeonosql.dto.ErrorDTO;
import com.amirun.abdtechdmeonosql.utils.RecipeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeUtilsTest {
    @Test
    void testGetErrorDTO() {
        String message = "Test Error";
        String exceptionMessage = "Exception Details";

        ErrorDTO errorDTO = RecipeUtils.getErrorDTO(message, exceptionMessage);

        assertEquals(message, errorDTO.getDescription());
        assertEquals(exceptionMessage, errorDTO.getErrorDetails());
    }
}