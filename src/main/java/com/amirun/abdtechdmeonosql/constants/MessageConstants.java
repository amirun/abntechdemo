package com.amirun.abdtechdmeonosql.constants;

/**
 * Constants used in for error messages
 */
public class MessageConstants {

    private MessageConstants() {
    }

    public static final String RECIPE_NOT_FOUND = "No recipe found for id ";
    public static final String NO_FILTERING_CRITERIA = "No filtering criteria specified.";
    public static final String DUPLICATE_RECIPE = "Error saving recipe. Duplicate name found.";
    public static final String INVALID_INPUT = "Invalid input.";
    public static final String FILTER_LOGGER = "Filtered data size: {}";
}
