package com.foodflow.repository;

import com.foodflow.entity.FoodIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodIngredientRepository extends JpaRepository<FoodIngredient, Long> {
    List<FoodIngredient> findByFoodItemId(Long foodItemId);
}