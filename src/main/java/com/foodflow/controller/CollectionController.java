package com.foodflow.controller;

import com.foodflow.dto.CollectionDtos.FavoriteResponse;
import com.foodflow.dto.CollectionDtos.LikedDishResponse;
import com.foodflow.entity.User;
import com.foodflow.service.FavoriteService;
import com.foodflow.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final FavoriteService favoriteService;
    private final RatingService ratingService;

    @GetMapping("/favorites")
    public List<FavoriteResponse> getFavorites(@AuthenticationPrincipal User customer) {
        return favoriteService.getMyFavorites(customer);
    }

    @PostMapping("/favorites/{restaurantId}")
    public void addFavorite(@PathVariable Long restaurantId, @AuthenticationPrincipal User customer) {
        favoriteService.addFavorite(customer, restaurantId);
    }

    @DeleteMapping("/favorites/{restaurantId}")
    public void removeFavorite(@PathVariable Long restaurantId, @AuthenticationPrincipal User customer) {
        favoriteService.removeFavorite(customer, restaurantId);
    }

    @GetMapping("/liked-dishes")
    public List<LikedDishResponse> getLikedDishes(@AuthenticationPrincipal User customer) {
        return ratingService.getLikedDishes(customer);
    }
}