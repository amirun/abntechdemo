package com.amirun.abdtechdmeonosql.service;

import com.amirun.abdtechdmeonosql.constants.MessageConstants;
import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.exception.DuplicateRecipeException;
import com.amirun.abdtechdmeonosql.exception.InvalidFilterException;
import com.amirun.abdtechdmeonosql.exception.RecipeNotFoundException;
import com.amirun.abdtechdmeonosql.model.QRecipe;
import com.amirun.abdtechdmeonosql.model.Recipe;
import com.amirun.abdtechdmeonosql.repository.RecipeRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{
    private final Logger logger = LoggerFactory.getLogger(RecipeServiceImpl.class);
    private final RecipeRepository recipeRepository;

    /**
     * {@inheritDoc}
     */
    @Transactional
    public Recipe createRecipe(RecipeDTO dto) throws DuplicateRecipeException {
        logger.debug("Creating recipe for dto: {}", dto);
        Recipe recipe = dto.toEntity();
        try{
            return recipeRepository.save(recipe);
        } catch (DuplicateKeyException e) {
            logger.error(MessageConstants.DUPLICATE_RECIPE, e);
            throw new DuplicateRecipeException(dto.getName());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Transactional
    public Recipe updateRecipe(String id, Recipe recipe) throws RecipeNotFoundException {
        logger.debug("Updating recipe for id: {}", id);
        Recipe existingRecipe = getRecipeById(id);
        recipe.setId(existingRecipe.getId());
        return recipeRepository.save(recipe);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public boolean deleteRecipe(String id) throws RecipeNotFoundException {
        logger.debug("deleting recipe by id: {}", id);
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Recipe getRecipeById(String id) throws RecipeNotFoundException {
        logger.debug("Searching for recipe by id: {}", id);
        return recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(MessageConstants.RECIPE_NOT_FOUND+": {}", id);
                    return new RecipeNotFoundException(MessageConstants.RECIPE_NOT_FOUND + id);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Recipe> getAllRecipes(int pageNo, int size) {
        logger.debug("Get all recipes for page: {}, size: {}", pageNo, size);
        return recipeRepository.findAll(PageRequest.of(pageNo, size)).getContent();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Recipe> filterRecipe(Boolean isVegetarian, Integer numberOfServings, List<String> ingredientsToInclude, List<String> ingredientsToExclude, String instructionTextFilter) throws InvalidFilterException {
        logger.debug("Filtering recipes based on conditions: isVegetarian={}, numberOfServings={}, ingredientsToInclude={}, ingredientsToExclude={}, instructionTextFilter={}",
                isVegetarian, numberOfServings, ingredientsToInclude, ingredientsToExclude, instructionTextFilter);
        QRecipe query = QRecipe.recipe;
        BooleanExpression finalCondition = null;
        if (isVegetarian != null) {
            finalCondition = Boolean.TRUE.equals(isVegetarian) ? query.isVegetarian.isTrue() : query.isVegetarian.isFalse();
        }
        if (numberOfServings != null) {
            BooleanExpression servingNo = query.numberOfServings.eq(numberOfServings);
            finalCondition = addFinalCondition(finalCondition, servingNo);
        }
        if (!CollectionUtils.isEmpty(ingredientsToInclude)) {

            //This section enables case-insensitive search of ingredients, however this can be a very expensive and does a full scan

            /*BooleanExpression ingredientIncl = null;
            for (String ingredient : filterDTO.getIngredientsToInclude()) {
                ingredientIncl = ingredientIncl == null
                        ? query.ingredients.any().containsIgnoreCase(ingredient)
                        : ingredientIncl.and(query.ingredients.any().containsIgnoreCase(ingredient) );
            }*/
            BooleanExpression ingredientIncl = query.ingredients.any().in(ingredientsToInclude);
            finalCondition = addFinalCondition(finalCondition, ingredientIncl);

        }
        if (!CollectionUtils.isEmpty(ingredientsToExclude)) {
            // This section enables case-insensitive exclusion of ingredients, however this can be a very expensive with full scan

            /*BooleanExpression ingredientExcl = null;
            for (String ingredient : filterDTO.getIngredientsToExclude()) {
                ingredientExcl = ingredientExcl == null
                        ? query.ingredients.any().containsIgnoreCase(ingredient)
                        : ingredientExcl.and(query.ingredients.any().containsIgnoreCase(ingredient));
            }*/
            BooleanExpression ingredientExcl = query.ingredients.any().notIn(ingredientsToExclude);
            finalCondition = addFinalCondition(finalCondition, ingredientExcl);
        }

        if (StringUtils.hasLength(instructionTextFilter)) {
            // This section enables case-insensitive exclusion of ingredients.

            BooleanExpression instruction = query.instructions.containsIgnoreCase(instructionTextFilter);
            finalCondition = addFinalCondition(finalCondition, instruction);
        }

        if(finalCondition == null) {
            logger.debug(MessageConstants.NO_FILTERING_CRITERIA);
            throw new InvalidFilterException(MessageConstants.NO_FILTERING_CRITERIA);
        }

        List<Recipe> data = (List<Recipe>) recipeRepository.findAll(finalCondition);
        logger.info(MessageConstants.FILTER_LOGGER, data.size());

        return data;
    }

    /**
     * Adds a BooleanExpression to a final condition using the 'AND' logical operator.
     *
     * This method takes two boolean expressions, a 'finalCondition' and an 'addCondition', and combines them using the
     * 'AND' logical operator. If 'finalCondition' is null, it returns 'addCondition' as the final result.
     *
     * @param finalCondition The final boolean condition to which 'addCondition' will be added.
     * @param addCondition   The boolean condition to be added to the 'finalCondition'.
     * @return A BooleanExpression representing the result of combining 'finalCondition' and 'addCondition' using 'AND'.
     */
    BooleanExpression addFinalCondition(BooleanExpression finalCondition, BooleanExpression addCondition) {
        return finalCondition == null ? addCondition : finalCondition.and(addCondition);
    }
}
