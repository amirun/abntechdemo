package com.amirun.abdtechdmeonosql.it;

import com.amirun.abdtechdmeonosql.TestAbnTechDemoNosqlApplication;
import com.amirun.abdtechdmeonosql.constants.MessageConstants;
import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.model.Recipe;
import com.amirun.abdtechdmeonosql.repository.RecipeRepository;
import com.amirun.abdtechdmeonosql.utils.TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestAbnTechDemoNosqlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(username = "user")
class RecipeControllerIT {

    private final String PATH = "/recipes";
    private final String PATH_SLASH = PATH+"/";

    @Autowired
    private WebApplicationContext context;

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    public void setup() {
        //Adding 4 recipes for testing
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        recipeRepository.saveAll(TestUtils.createRecipeList());
    }

    @AfterEach
    void tearDown() {
        //clearing db after each test
        recipeRepository.deleteAll();
    }


    @Test
    void createRecipe_Success() throws Exception {

        RecipeDTO dto = TestUtils.createRecipeDTO();

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andDo(print());

        assertThat(recipeRepository.count()).isEqualTo(5L);
    }

    @WithAnonymousUser
    @Test
    void createRecipe_FailAuth() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void createRecipe_FailDuplicateRecipe() throws Exception {

        Page<Recipe> page = recipeRepository.findAll(Pageable.ofSize(1));
        Recipe recipe = page.getContent().get(0);
        RecipeDTO dto = RecipeDTO.builder()
                .name(recipe.getName())
                .isVegetarian(false)
                .instructions(recipe.getInstructions())
                .ingredients(recipe.getIngredients())
                .numberOfServings(4)
                .build();

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.description").value(MessageConstants.DUPLICATE_RECIPE))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString(dto.getName())))
                .andDo(print());


        assertThat(recipeRepository.count()).isEqualTo(4L);
    }

    @Test
    void createRecipe_FailServingsBadRequest() throws Exception {

        RecipeDTO dto = TestUtils.createRecipeDTO();
        dto.setNumberOfServings(0);
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value(MessageConstants.INVALID_INPUT))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString("Servings")))
                .andDo(print());

        assertThat(recipeRepository.count()).isEqualTo(4L);
    }

    @Test
    void createRecipe_FailNameBadRequest() throws Exception {

        RecipeDTO dto = TestUtils.createRecipeDTO();
        dto.setName("");
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value(MessageConstants.INVALID_INPUT))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString("Name")))
                .andDo(print());

        assertThat(recipeRepository.count()).isEqualTo(4L);
    }

    @Test
    void updateRecipe_Success() throws Exception {

        Page<Recipe> page = recipeRepository.findAll(Pageable.ofSize(1));
        Recipe dto = page.getContent().get(0);
        dto.setName("New Name");
        dto.getIngredients().add("IG4");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(PATH_SLASH+dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andDo(print());

        Recipe saved = recipeRepository.findById(dto.getId()).orElse(new Recipe());
        assertThat(saved.getIngredients()).containsAll(dto.getIngredients());
        assertThat(recipeRepository.count()).isEqualTo(4L);
    }

    @Test
    void updateRecipe_FailNoInstructionsBadRequest() throws Exception {

        Page<Recipe> page = recipeRepository.findAll(Pageable.ofSize(1));
        Recipe dto = page.getContent().get(0);
        dto.setInstructions(null);
        String message = "Recipe must have instructions.";

        mockMvc.perform(MockMvcRequestBuilders
                        .put(PATH_SLASH+dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value(MessageConstants.INVALID_INPUT))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString(message)))
                .andDo(print());

        Recipe dbRecipe = recipeRepository.findById(dto.getId()).orElse(new Recipe());
        assertThat(dbRecipe.getInstructions()).isNotBlank();
    }

    @Test
    void updateRecipe_FailNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put(PATH_SLASH+TestUtils.getUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(TestUtils.createRecipeDTO())))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getRecipeById_Success() throws Exception {

        Page<Recipe> page = recipeRepository.findAll(Pageable.ofSize(1));
        Recipe recipe = page.getContent().get(0);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH_SLASH+recipe.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipe.getId()))
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andDo(print());
    }

    @Test
    void getRecipeById_FailNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH_SLASH+TestUtils.getUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value(MessageConstants.RECIPE_NOT_FOUND))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString(MessageConstants.RECIPE_NOT_FOUND)))
                .andDo(print());
    }

    @Test
    void deleteRecipe_Success() throws Exception {
        Page<Recipe> page = recipeRepository.findAll(Pageable.ofSize(1));
        Recipe recipe = page.getContent().get(0);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(PATH_SLASH+recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());

        assertThat(recipeRepository.count()).isEqualTo(3L);

    }

    @Test
    void deleteRecipe_FailNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(PATH_SLASH+TestUtils.getUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value(MessageConstants.RECIPE_NOT_FOUND))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString(MessageConstants.RECIPE_NOT_FOUND)))
                .andDo(print());

        assertThat(recipeRepository.count()).isEqualTo(4L);

    }

    @Test
    void filterRecipe_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recipes/filterRecipes")
                        .param("isVegetarian", "true")
                        .param("numberOfServings", "3")
                        .param("ingredientsToInclude", "Onion")
                        .param("ingredientsToInclude", "Salt")
                        .param("ingredientsToExclude", "Garlic")
                        .param("instructionTextFilter", "oven"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Recipe1"))
                .andExpect(jsonPath("$[0].isVegetarian").value(true))
                .andExpect(jsonPath("$[0].numberOfServings").value(3))
                .andExpect(jsonPath("$[0].ingredients", Matchers.hasItems("Onion","Salt")))
                .andExpect(jsonPath("$[0].ingredients", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].instructions").value("Text instructions: Uses oven"));
    }

    @Test
    void filterRecipe_FailInvalidFilter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recipes/filterRecipes")
                        .param("isVegetarian", "")
                        .param("numberOfServings", "")
                        .param("ingredientsToInclude", "")
                        .param("ingredientsToExclude", "")
                        .param("instructionTextFilter", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(MessageConstants.INVALID_INPUT))
                .andExpect(jsonPath("$.errorDetails", Matchers.containsString(MessageConstants.NO_FILTERING_CRITERIA)))
                .andDo(print());
    }


}
