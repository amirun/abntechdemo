package com.amirun.abdtechdmeonosql.ut.controller;

import com.amirun.abdtechdmeonosql.controller.RecipeController;
import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.exception.DuplicateRecipeException;
import com.amirun.abdtechdmeonosql.exception.InvalidFilterException;
import com.amirun.abdtechdmeonosql.exception.RecipeNotFoundException;
import com.amirun.abdtechdmeonosql.model.Recipe;
import com.amirun.abdtechdmeonosql.service.RecipeService;
import com.amirun.abdtechdmeonosql.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    private final String PATH = "/recipes";

    @InjectMocks
    private RecipeController recipeController;

    @Mock
    private RecipeService recipeService;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
        mapper = new ObjectMapper();
    }

    @Test
    void createRecipe_Success() throws Exception {
        RecipeDTO recipeDTO = TestUtils.createRecipeDTO();
        Recipe createdRecipe = TestUtils.createRecipe();

        when(recipeService.createRecipe(any(RecipeDTO.class))).thenReturn(createdRecipe);

        this.mockMvc.perform(post(PATH)
                        .content(mapper.writeValueAsString(recipeDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdRecipe.getId()))
                .andExpect(jsonPath("$.name").value(createdRecipe.getName()))
                .andExpect(jsonPath("$.instructions").value(createdRecipe.getInstructions()))
                .andDo(print());
    }

    @Test
    void createRecipe_FailWhenDuplicateName() throws Exception {
        // Mock the service method to throw exception
        RecipeDTO recipeDTO = TestUtils.createRecipeDTO();
        when(recipeService.createRecipe(any(RecipeDTO.class))).thenThrow(DuplicateRecipeException.class);

        try{
            this.mockMvc.perform(post(PATH)
                    .content(mapper.writeValueAsString(recipeDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (ServletException e){
            Assertions.assertThat(e.getMessage()).contains(DuplicateRecipeException.class.getName());
        }
    }

    @Test
    void updateRecipe_Success() throws Exception {
        String recipeId = TestUtils.getUUID();
        RecipeDTO recipeDTO = TestUtils.createRecipeDTO();
        Recipe updatedRecipe = TestUtils.createRecipe();
        updatedRecipe.setId(recipeId);
        when(recipeService.updateRecipe(eq(recipeId), any(Recipe.class))).thenReturn(updatedRecipe);

        this.mockMvc.perform(put(PATH + "/" + recipeId)
                        .content(mapper.writeValueAsString(recipeDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedRecipe.getId()))
                .andExpect(jsonPath("$.name").value(updatedRecipe.getName()))
                .andExpect(jsonPath("$.instructions").value(updatedRecipe.getInstructions()))
                .andDo(print());
    }


    @Test
    void updateRecipe_FailWithInvalidId() throws Exception {
        String recipeId = TestUtils.getUUID();
        RecipeDTO recipeDTO = TestUtils.createRecipeDTO();
        when(recipeService.updateRecipe(eq(recipeId), any(Recipe.class))).thenThrow(RecipeNotFoundException.class);

        try {
            this.mockMvc.perform(put(PATH + "/" + recipeId)
                    .content(mapper.writeValueAsString(recipeDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (ServletException e) {
            Assertions.assertThat(e.getMessage()).contains(RecipeNotFoundException.class.getName());
        }
    }

    @Test
    void deleteRecipe_Success() throws Exception {
        String recipeId = TestUtils.getUUID();

        when(recipeService.deleteRecipe(recipeId)).thenReturn(true);

        this.mockMvc.perform(delete(PATH + "/" + recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    void getRecipeById_Success() throws Exception {
        String recipeId = TestUtils.getUUID();
        Recipe recipe = TestUtils.createRecipe();

        when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

        this.mockMvc.perform(get(PATH + "/" + recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipe.getId()))
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andExpect(jsonPath("$.instructions").value(recipe.getInstructions()))
                .andDo(print());
    }

    @Test
    void getAllRecipes_Success() throws Exception {
        int pageNo = 1;
        int size = 10;
        List<Recipe> recipes = Arrays.asList(TestUtils.createRecipe(), TestUtils.createRecipe(), TestUtils.createRecipe());

        when(recipeService.getAllRecipes(pageNo, size)).thenReturn(recipes);

        this.mockMvc.perform(get(PATH)
                        .param("pageNo", String.valueOf(pageNo))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(recipes.size()))
                .andDo(print());
    }

    @Test
    void filterRecipes_Success() throws Exception {
        // Prepare query parameters for filtering
        Boolean isVegetarian = true;
        Integer numberOfServings = 4;
        List<String> ingredientsToInclude = Arrays.asList("ing1", "ing2");
        List<String> ingredientsToExclude = Arrays.asList("ing3", "ing4");
        String instructionTextFilter = "instructions";

        // Create a list of recipes that match the filter criteria
        List<Recipe> filteredRecipes = Arrays.asList(TestUtils.createRecipe(), TestUtils.createRecipe());

        // Mock the service method to return the filtered recipes
        when(recipeService.filterRecipe(isVegetarian, numberOfServings, ingredientsToInclude, ingredientsToExclude, instructionTextFilter))
                .thenReturn(filteredRecipes);

        this.mockMvc.perform(get(PATH + "/filterRecipes")
                        .param("isVegetarian", String.valueOf(isVegetarian))
                        .param("numberOfServings", String.valueOf(numberOfServings))
                        .param("ingredientsToInclude", "ing1,ing2")
                        .param("ingredientsToExclude", "ing3,ing4")
                        .param("instructionTextFilter", instructionTextFilter)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(filteredRecipes.size()))
                .andDo(print());
    }

    @Test
    void filterRecipes_FailWhenNoFilterConditions() throws Exception {
        when(recipeService.filterRecipe(null, null, null, null, null))
                .thenThrow(InvalidFilterException.class);

        try{
            this.mockMvc.perform(get(PATH + "/filterRecipes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (Exception e){
            Assertions.assertThat(e.getMessage()).contains(InvalidFilterException.class.getName());
        }
    }

}
