package com.foodflow.service;

import com.foodflow.dto.RestaurantDtos.CreateRestaurantRequest;
import com.foodflow.dto.RestaurantDtos.RestaurantResponse;
import com.foodflow.entity.Restaurant;
import com.foodflow.entity.RestaurantStatus;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantResponse createRestaurant(CreateRestaurantRequest request, User owner) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cuisineType(request.getCuisineType())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .pincode(request.getPincode())
                .openingTime(request.getOpeningTime() != null ? LocalTime.parse(request.getOpeningTime()) : null)
                .closingTime(request.getClosingTime() != null ? LocalTime.parse(request.getClosingTime()) : null)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imageUrl(request.getImageUrl())
                .status(RestaurantStatus.CLOSED)
                .averageRating(0.0)
                .owner(owner)
                .build();

        restaurantRepository.save(restaurant);
        return toResponse(restaurant);
    }

    public RestaurantResponse updateStatus(Long restaurantId, RestaurantStatus status, User owner) {
        Restaurant restaurant = getOwnedRestaurantOrThrow(restaurantId, owner);
        restaurant.setStatus(status);
        restaurantRepository.save(restaurant);
        return toResponse(restaurant);
    }

    public RestaurantResponse updateLocation(Long restaurantId, Double latitude, Double longitude, User owner) {
        Restaurant restaurant = getOwnedRestaurantOrThrow(restaurantId, owner);
        restaurant.setLatitude(latitude);
        restaurant.setLongitude(longitude);
        restaurantRepository.save(restaurant);
        return toResponse(restaurant);
    }

    public RestaurantResponse updateImage(Long restaurantId, String imageUrl, User owner) {
        Restaurant restaurant = getOwnedRestaurantOrThrow(restaurantId, owner);
        restaurant.setImageUrl(imageUrl);
        restaurantRepository.save(restaurant);
        return toResponse(restaurant);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RestaurantResponse getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Restaurant not found"));
        return toResponse(restaurant);
    }

    public List<RestaurantResponse> getMyRestaurants(User owner) {
        return restaurantRepository.findByOwnerId(owner.getId()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public Restaurant getOwnedRestaurantOrThrow(Long restaurantId, User owner) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> ApiException.notFound("Restaurant not found"));

        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw ApiException.forbidden("You do not own this restaurant");
        }
        return restaurant;
    }

    private RestaurantResponse toResponse(Restaurant r) {
        return new RestaurantResponse(
                r.getId(), r.getName(), r.getDescription(), r.getCuisineType(),
                r.getAddressLine(), r.getCity(), r.getPincode(), r.getStatus(),
                r.getOpeningTime(), r.getClosingTime(), r.getAverageRating(), r.getOwner().getId(),
                r.getLatitude(), r.getLongitude(), r.getImageUrl()
        );
    }
}