package com.amirun.abdtechdmeonosql.service;

import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.exception.DuplicateRecipeException;
import com.amirun.abdtechdmeonosql.exception.InvalidFilterException;
import com.amirun.abdtechdmeonosql.exception.RecipeNotFoundException;
import com.amirun.abdtechdmeonosql.model.Recipe;

import java.util.List;

public interface RecipeService {
    /**
     * Create a new recipe based on the provided DTO.
     *
     * @param dto The DTO {@link RecipeDTO} representing the recipe.
     * @return The created recipe.
     * @throws DuplicateRecipeException If a recipe with the same name already exists.
     */
    Recipe createRecipe(RecipeDTO dto) throws RecipeNotFoundException, DuplicateRecipeException;

    /**
     * Update an existing recipe with the provided ID.
     *
     * @param id     The ID of the recipe to update.
     * @param recipe The updated recipe information.
     * @return The modified recipe.
     * @throws RecipeNotFoundException If no recipe is found with the given ID.
     */
    Recipe updateRecipe(String id, Recipe recipe) throws RecipeNotFoundException;

    /**
     * Delete a recipe with the provided ID.
     *
     * @param id The ID of the recipe to delete.
     * @return True if the recipe was deleted successfully.
     * @throws RecipeNotFoundException If no recipe is found with the given ID.
     */
   boolean deleteRecipe(String id) throws RecipeNotFoundException;

    /**
     * Get a recipe by its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return The recipe with the given ID.
     * @throws RecipeNotFoundException If no recipe is found with the given ID.
     */
    Recipe getRecipeById(String id) throws RecipeNotFoundException;

    /**
     * Get a list of recipes with pagination.
     *
     * @param pageNo The page number.
     * @param size   The number of recipes per page.
     * @return A list of recipes for the specified page.
     */
    List<Recipe> getAllRecipes(int pageNo, int size);

    /**
     * Filter recipes based on specified criteria.
     *
     * @param isVegetarian         Whether the recipe is vegetarian.
     * @param numberOfServings     The number of servings.
     * @param ingredientsToInclude List of ingredients to include.
     * @param ingredientsToExclude List of ingredients to exclude.
     * @param instructionTextFilter Text to filter recipes by instructions.
     * @return A list of filtered recipes.
     * @throws InvalidFilterException If no valid filtering criteria are specified.
     */
    List<Recipe> filterRecipe(Boolean isVegetarian, Integer numberOfServings, List<String> ingredientsToInclude, List<String> ingredientsToExclude, String instructionTextFilter) throws InvalidFilterException;
}
