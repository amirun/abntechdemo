package com.amirun.abdtechdmeonosql.ut.service;

import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.exception.DuplicateRecipeException;
import com.amirun.abdtechdmeonosql.exception.InvalidFilterException;
import com.amirun.abdtechdmeonosql.exception.RecipeNotFoundException;
import com.amirun.abdtechdmeonosql.model.Recipe;
import com.amirun.abdtechdmeonosql.repository.RecipeRepository;
import com.amirun.abdtechdmeonosql.service.RecipeServiceImpl;
import com.amirun.abdtechdmeonosql.utils.TestUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {
    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Test
    void createRecipe_Success() {
        RecipeDTO recipeDTO = TestUtils.createRecipeDTO();
        recipeDTO.setName("Test Recipe");

        Recipe recipe = TestUtils.createRecipe();
        recipe.setId("1");
        recipe.setName("Test Recipe");

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        assertDoesNotThrow(() -> {
            Recipe createdRecipe = recipeService.createRecipe(recipeDTO);
            assertNotNull(createdRecipe);
            assertEquals("1", createdRecipe.getId());
            assertEquals("Test Recipe", createdRecipe.getName());
        });
    }

    @Test
    void createRecipe_FailDuplicate() {
        RecipeDTO recipeDTO = TestUtils.createRecipeDTO();
        recipeDTO.setName("Duplicate Recipe");

        when(recipeRepository.save(any(Recipe.class)))
                .thenThrow(new DuplicateKeyException("Duplicate key"));

        assertThrows(DuplicateRecipeException.class, () -> recipeService.createRecipe(recipeDTO));
    }

    @Test
    void updateRecipe_Success() {
        Recipe existingRecipe = TestUtils.createRecipe();
        existingRecipe.setName("Existing Recipe");

        String recipeId = existingRecipe.getId();

        Recipe updatedRecipe = TestUtils.createRecipe();
        updatedRecipe.setId(recipeId);
        updatedRecipe.setName("Updated Recipe");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);

        assertDoesNotThrow(() -> {
            Recipe result = recipeService.updateRecipe(recipeId, updatedRecipe);
            assertNotNull(result);
            assertEquals(existingRecipe.getId(), result.getId());
            assertEquals(updatedRecipe.getName(), result.getName());
        });
    }

    @Test
    void updateRecipe_FailRecipeNotFound() {
        Recipe recipe = TestUtils.createRecipe();

        when(recipeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.updateRecipe(TestUtils.getUUID(), recipe));
    }


    @Test
    void deleteRecipe_Success() {
        Recipe existingRecipe = TestUtils.createRecipe();
        String recipeId = existingRecipe.getId();

        when(recipeRepository.findById(existingRecipe.getId())).thenReturn(Optional.of(existingRecipe));
        assertDoesNotThrow(() -> {
            assertTrue(recipeService.deleteRecipe(recipeId));
            verify(recipeRepository, times(1)).delete(existingRecipe);
        });
    }

    @Test
    void deleteRecipe_FailRecipeNotFound() {
        String recipeId = TestUtils.getUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipe(recipeId));
    }

    @Test
    void getRecipeById_Success() {
        Recipe recipe = TestUtils.createRecipe();
        recipe.setName("Test Recipe");

        String recipeId = recipe.getId();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertDoesNotThrow(() -> {
            Recipe result = recipeService.getRecipeById(recipeId);
            assertNotNull(result);
            assertEquals(recipeId, result.getId());
            assertEquals("Test Recipe", result.getName());
        });
    }

    @Test
    void getRecipeById_FailRecipeNotFound() {
        String recipeId = TestUtils.getUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById(recipeId));
    }

    @Test
    void getAllRecipes_Success() {
        int pageNo = 0;
        int size = 10;
        List<Recipe> recipeList = List.of(TestUtils.createRecipe(), TestUtils.createRecipe());
        Page foundPage = new PageImpl<Recipe>(recipeList);
        when(recipeRepository.findAll(PageRequest.of(pageNo, size))).thenReturn(foundPage);
        List<Recipe> result = recipeService.getAllRecipes(pageNo, size);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void filterRecipe_Success() {

        Recipe recipe1 = TestUtils.createRecipe();
        Recipe recipe2 = TestUtils.createRecipe();
        recipe2.setIsVegetarian(false);

        List<Recipe> mockRecipes = List.of(recipe1, recipe2);

        when(recipeRepository.findAll(any(BooleanExpression.class))).thenReturn(mockRecipes);
        assertDoesNotThrow(() -> {
            List<Recipe> result = recipeService.filterRecipe(true, 2, List.of("ing1", "ing2"), List.of("ing3", "ing4"), "test");
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(recipe1.getId(), result.get(0).getId());
        });

    }

    @Test
    void filterRecipe_FailInvalidFilter() {
        assertThrows(InvalidFilterException.class, () -> recipeService.filterRecipe(null, null, null, null, null));
    }
}