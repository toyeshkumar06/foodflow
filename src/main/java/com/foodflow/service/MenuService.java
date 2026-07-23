package com.foodflow.service;

import com.foodflow.dto.MenuDtos.*;
import com.foodflow.entity.*;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final RestaurantService restaurantService;
    private final FoodCategoryRepository categoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodIngredientRepository foodIngredientRepository;

    public CategoryResponse createCategory(Long restaurantId, CreateCategoryRequest request, User owner) {
        Restaurant restaurant = restaurantService.getOwnedRestaurantOrThrow(restaurantId, owner);
        FoodCategory category = FoodCategory.builder().name(request.getName()).restaurant(restaurant).build();
        categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }

    public FoodItemResponse updateImage(Long foodItemId, String imageUrl, User owner) {
    FoodItem item = foodItemRepository.findById(foodItemId)
            .orElseThrow(() -> ApiException.notFound("Food item not found"));
    restaurantService.getOwnedRestaurantOrThrow(item.getRestaurant().getId(), owner);
    item.setImageUrl(imageUrl);
    foodItemRepository.save(item);
    return toFoodItemResponse(item);
    }

    public FoodItemResponse createFoodItem(Long restaurantId, CreateFoodItemRequest request, User owner) {
        Restaurant restaurant = restaurantService.getOwnedRestaurantOrThrow(restaurantId, owner);

        FoodCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> ApiException.notFound("Category not found"));

        if (!category.getRestaurant().getId().equals(restaurant.getId())) {
            throw ApiException.badRequest("This category does not belong to this restaurant");
        }

        FoodItem item = FoodItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .veg(request.isVeg())
                .available(true)
                .imageUrl(request.getImageUrl())
                .category(category)
                .restaurant(restaurant)
                .build();

        foodItemRepository.save(item);
        return toFoodItemResponse(item);
    }

    public FoodItemResponse updateAvailability(Long foodItemId, boolean available, User owner) {
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> ApiException.notFound("Food item not found"));
        restaurantService.getOwnedRestaurantOrThrow(item.getRestaurant().getId(), owner);
        item.setAvailable(available);
        foodItemRepository.save(item);
        return toFoodItemResponse(item);
    }

    public void deleteFoodItem(Long foodItemId, User owner) {
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> ApiException.notFound("Food item not found"));
        restaurantService.getOwnedRestaurantOrThrow(item.getRestaurant().getId(), owner);
        foodItemRepository.delete(item);
    }

    public List<CategoryResponse> getCategories(Long restaurantId) {
    return categoryRepository.findByRestaurantId(restaurantId).stream()
            .map(c -> new CategoryResponse(c.getId(), c.getName()))
            .collect(Collectors.toList());
    }
    public List<FoodItemResponse> getMenu(Long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId).stream()
                .map(this::toFoodItemResponse).collect(Collectors.toList());
    }

    public Ingredient addIngredient(Long restaurantId, CreateIngredientRequest request, User owner) {
        Restaurant restaurant = restaurantService.getOwnedRestaurantOrThrow(restaurantId, owner);
        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .quantityAvailable(request.getQuantityAvailable())
                .restaurant(restaurant)
                .build();
        return ingredientRepository.save(ingredient);
    }

    public FoodIngredient linkIngredientToFoodItem(Long foodItemId, LinkIngredientRequest request, User owner) {
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> ApiException.notFound("Food item not found"));
        restaurantService.getOwnedRestaurantOrThrow(item.getRestaurant().getId(), owner);

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> ApiException.notFound("Ingredient not found"));

        if (!ingredient.getRestaurant().getId().equals(item.getRestaurant().getId())) {
            throw ApiException.badRequest("This ingredient does not belong to this restaurant");
        }

        FoodIngredient link = FoodIngredient.builder()
                .foodItem(item).ingredient(ingredient).quantityRequired(request.getQuantityRequired()).build();
        return foodIngredientRepository.save(link);
    }

    private FoodItemResponse toFoodItemResponse(FoodItem item) {
        return new FoodItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                item.isVeg(), item.isAvailable(), item.getCategory().getName(), item.getImageUrl()
        );
    }
}