package com.amirun.abdtechdmeonosql.controller;

import com.amirun.abdtechdmeonosql.dto.ErrorDTO;
import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.exception.DuplicateRecipeException;
import com.amirun.abdtechdmeonosql.exception.InvalidFilterException;
import com.amirun.abdtechdmeonosql.exception.RecipeNotFoundException;
import com.amirun.abdtechdmeonosql.model.Recipe;
import com.amirun.abdtechdmeonosql.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class RecipeController {

    private final RecipeService recipeService;
    private final Logger logger = LoggerFactory.getLogger(RecipeController.class);


    @Operation(description = "Create a recipe.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Returns the recipe created.",
                            content = {@Content(mediaType = "application/json",
                                schema = @Schema(implementation = Recipe.class))}),
                    @ApiResponse(responseCode = "409",
                            description = "When recipe with same name exists",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDTO.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDTO.class))})
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> createRecipe(@RequestBody @Valid RecipeDTO recipe) throws DuplicateRecipeException, RecipeNotFoundException {
        Recipe createdRecipe = recipeService.createRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @Operation(description = "Update existing recipe.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Returns the modified recipe.",
                            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Recipe.class))}),
                    @ApiResponse(responseCode = "409",
                            description = "When recipe with same name exists.",
                            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorDTO.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input",
                            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorDTO.class))})
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody @Valid RecipeDTO dto) throws RecipeNotFoundException {
        logger.debug("request dto: {}", dto);
        Recipe updatedRecipe = recipeService.updateRecipe(id, dto.toEntity());
        return ResponseEntity.ok(updatedRecipe);
    }

    @Operation(description = "Delete a recipe specified by id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "True when delete is successful."),
                    @ApiResponse(responseCode = "404",
                            description = "If no matching recipe is found.",
                            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorDTO.class))})
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRecipe(@PathVariable String id) throws RecipeNotFoundException {
        boolean deleted = recipeService.deleteRecipe(id);
        return  ResponseEntity.ok(deleted);
    }


    @Operation(description = "Fetch recipe matching given id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Returns the recipe matching the given id.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Recipe.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "If no matching recipe is found.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDTO.class))})
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) throws RecipeNotFoundException {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }


    @Operation(description = "Get all recipes with pagination.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Returns a list of recipes based on pagination.",
                            content = {@Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Recipe.class)))})
            }
    )
    @GetMapping(params = { "pageNo", "size" })
    public ResponseEntity<List<Recipe>> getAllRecipes(@RequestParam("pageNo") int pageNo, @RequestParam("size") int size) {
        List<Recipe> recipes = recipeService.getAllRecipes(pageNo, size);
        return ResponseEntity.ok(recipes);
    }


    //params instead of DTO as number of Params do not exceed 7
    @Operation(description = "Filter recipes based on specified parameters.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Returns a list of filtered recipes.",
                            content = {@Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Recipe.class)))}),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDTO.class))})
            }
    )
    @GetMapping("/filterRecipes")
    public ResponseEntity<List<Recipe>> filterRecipes(
                                                      @RequestParam(required = false) Boolean isVegetarian,
                                                      @RequestParam(required = false) @Size(min = 1, max = 100) Integer numberOfServings,
                                                      @RequestParam(required = false) List<String> ingredientsToInclude,
                                                      @RequestParam(required = false) List<String> ingredientsToExclude,
                                                      @RequestParam(required = false) String instructionTextFilter

    ) throws InvalidFilterException {
        List<Recipe> recipes =  recipeService.filterRecipe(isVegetarian, numberOfServings, ingredientsToInclude, ingredientsToExclude, instructionTextFilter);
        return ResponseEntity.ok(recipes);
    }

}
