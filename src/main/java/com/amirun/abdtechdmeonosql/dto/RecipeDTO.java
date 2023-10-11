package com.amirun.abdtechdmeonosql.dto;

import com.amirun.abdtechdmeonosql.model.Recipe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RecipeDTO {
    @NotBlank(message = "Name cannot be empty.")
    private String name;

    @NotNull(message = "true/false flag to indicate is recipe is vegetarian.")
    private Boolean isVegetarian;

    @Range(min = 1, max = 100, message = "Servings can be in range 1 - 100.")
    private Integer numberOfServings;

    @Size(message = "Recipe must have instructions.", min = 1, max = 1500)
    @NotNull(message = "Recipe must have instructions.")
    private String instructions;

    @NotEmpty(message = "Ingredients list cannot be empty")
    private List<String> ingredients;

    public Recipe toEntity() {
        return Recipe.builder()
                .name(this.name)
                .isVegetarian(this.isVegetarian)
                .instructions(this.getInstructions())
                .numberOfServings(this.numberOfServings)
                .ingredients(ingredients)
                .build();
    }
}
