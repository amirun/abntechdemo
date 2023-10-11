package com.amirun.abdtechdmeonosql.utils;

import com.amirun.abdtechdmeonosql.dto.RecipeDTO;
import com.amirun.abdtechdmeonosql.model.Recipe;

import java.util.List;
import java.util.UUID;

public class TestUtils {


    public static RecipeDTO createRecipeDTO() {
        return RecipeDTO.builder()
                .name("Rec1")
                .numberOfServings(2)
                .ingredients(List.of("ing1","ing2"))
                .instructions("Text instructions")
                .isVegetarian(true)
                .build();
    }
    public static Recipe createRecipe() {
        return Recipe.builder()
                .id(UUID.randomUUID().toString())
                .name("Rec1")
                .numberOfServings(2)
                .ingredients(List.of("ing1","ing2"))
                .instructions("Text instructions")
                .isVegetarian(true)
                .build();
    }

    public static List<Recipe> createRecipeList() {
        Recipe r1 = Recipe.builder()
                .name("Recipe1")
                .numberOfServings(3)
                .ingredients(List.of("Onion", "Salt"))
                .instructions("Text instructions: Uses oven")
                .isVegetarian(true)
                .build();

        Recipe r2 = Recipe.builder()
                .name("Recipe2")
                .numberOfServings(3)
                .ingredients(List.of("Onion", "Garlic", "Salt", "Mushroom"))
                .instructions("Text instructions: uses pan")
                .isVegetarian(true)
                .build();

        Recipe r3 = Recipe.builder()
                .name("Recipe3")
                .numberOfServings(4)
                .ingredients(List.of("Onion", "Garlic", "Chicken"))
                .instructions("Text instructions: needs microwave")
                .isVegetarian(false)
                .build();

        Recipe r4 = Recipe.builder()
                .name("Recipe4")
                .numberOfServings(4)
                .ingredients(List.of("Butter", "flour", "eggs"))
                .instructions("Text instructions: uses pan")
                .isVegetarian(false)
                .build();

        return List.of(r1, r2, r3, r4);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
