package com.amirun.abdtechdmeonosql.model;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a recipe entity with various attributes.
 * This class is used to model recipe information, including its name, vegetarian status,
 * number of servings, instructions, and a list of ingredients.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder
@QueryEntity
@ToString
public class Recipe {
    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true, background = true)
    private String name;

    @NotNull
    private Boolean isVegetarian;

    private Integer numberOfServings;

    @NotNull
    private String instructions;

    @NotEmpty
    @Indexed
    private List<String> ingredients;
}

