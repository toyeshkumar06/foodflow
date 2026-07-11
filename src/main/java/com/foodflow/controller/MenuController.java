package com.foodflow.controller;

import com.foodflow.dto.MenuDtos.*;
import com.foodflow.entity.FoodIngredient;
import com.foodflow.entity.Ingredient;
import com.foodflow.entity.User;
import com.foodflow.service.MenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Menu")
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/api/restaurant-owner/restaurants/{restaurantId}/categories")
    public CategoryResponse createCategory(@PathVariable Long restaurantId,
                                            @Valid @RequestBody CreateCategoryRequest request,
                                            @AuthenticationPrincipal User owner) {
        return menuService.createCategory(restaurantId, request, owner);
    }

    @PostMapping("/api/restaurant-owner/restaurants/{restaurantId}/food-items")
    public FoodItemResponse createFoodItem(@PathVariable Long restaurantId,
                                            @Valid @RequestBody CreateFoodItemRequest request,
                                            @AuthenticationPrincipal User owner) {
        return menuService.createFoodItem(restaurantId, request, owner);
    }

    @PatchMapping("/api/restaurant-owner/food-items/{id}/availability")
    public FoodItemResponse updateAvailability(@PathVariable Long id,
                                                @Valid @RequestBody UpdateAvailabilityRequest request,
                                                @AuthenticationPrincipal User owner) {
        return menuService.updateAvailability(id, request.getAvailable(), owner);
    }

    @DeleteMapping("/api/restaurant-owner/food-items/{id}")
    public void deleteFoodItem(@PathVariable Long id, @AuthenticationPrincipal User owner) {
        menuService.deleteFoodItem(id, owner);
    }

    @PostMapping("/api/restaurant-owner/restaurants/{restaurantId}/ingredients")
    public Ingredient addIngredient(@PathVariable Long restaurantId,
                                     @Valid @RequestBody CreateIngredientRequest request,
                                     @AuthenticationPrincipal User owner) {
        return menuService.addIngredient(restaurantId, request, owner);
    }

    @PostMapping("/api/restaurant-owner/food-items/{foodItemId}/ingredients")
    public FoodIngredient linkIngredient(@PathVariable Long foodItemId,
                                          @Valid @RequestBody LinkIngredientRequest request,
                                          @AuthenticationPrincipal User owner) {
        return menuService.linkIngredientToFoodItem(foodItemId, request, owner);
    }

    @GetMapping("/api/restaurants/{restaurantId}/menu")
    public List<FoodItemResponse> getMenu(@PathVariable Long restaurantId) {
        return menuService.getMenu(restaurantId);
    }
}