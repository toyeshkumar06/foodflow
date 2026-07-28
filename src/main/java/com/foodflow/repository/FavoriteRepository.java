package com.foodflow.repository;

import com.foodflow.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndRestaurantId(Long userId, Long restaurantId);
    List<Favorite> findByUserIdOrderByIdDesc(Long userId);
    void deleteByUserIdAndRestaurantId(Long userId, Long restaurantId);
}