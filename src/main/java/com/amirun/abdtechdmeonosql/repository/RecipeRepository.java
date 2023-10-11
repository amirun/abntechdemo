package com.amirun.abdtechdmeonosql.repository;

import com.amirun.abdtechdmeonosql.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeRepository extends MongoRepository<Recipe, String>, QuerydslPredicateExecutor<Recipe> {
}
