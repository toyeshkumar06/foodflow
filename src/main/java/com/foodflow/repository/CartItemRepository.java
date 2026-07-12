package com.foodflow.repository;

import com.foodflow.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomerId(Long customerId);
    Optional<CartItem> findByCustomerIdAndFoodItemId(Long customerId, Long foodItemId);
    void deleteByCustomerId(Long customerId);
}