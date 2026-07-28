package com.foodflow.service;

import com.foodflow.dto.CollectionDtos.FavoriteResponse;
import com.foodflow.entity.Favorite;
import com.foodflow.entity.Restaurant;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.FavoriteRepository;
import com.foodflow.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;

    public void addFavorite(User customer, Long restaurantId) {
        if (favoriteRepository.findByUserIdAndRestaurantId(customer.getId(), restaurantId).isPresent()) {
            return;
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> ApiException.notFound("Restaurant not found"));
        favoriteRepository.save(Favorite.builder().user(customer).restaurant(restaurant).build());
    }

    public void removeFavorite(User customer, Long restaurantId) {
        favoriteRepository.deleteByUserIdAndRestaurantId(customer.getId(), restaurantId);
    }

    public List<FavoriteResponse> getMyFavorites(User customer) {
        return favoriteRepository.findByUserIdOrderByIdDesc(customer.getId()).stream()
                .map(f -> new FavoriteResponse(
                        f.getRestaurant().getId(), f.getRestaurant().getName(), f.getRestaurant().getImageUrl(),
                        f.getRestaurant().getCuisineType(), f.getRestaurant().getAverageRating()))
                .collect(Collectors.toList());
    }
}