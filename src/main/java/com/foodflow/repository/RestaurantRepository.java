package com.foodflow.repository;

import com.foodflow.entity.Restaurant;
import com.foodflow.entity.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByStatus(RestaurantStatus status);
    List<Restaurant> findByOwnerId(Long ownerId);
}