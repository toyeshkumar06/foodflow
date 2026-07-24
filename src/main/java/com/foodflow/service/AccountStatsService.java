package com.foodflow.service;

import com.foodflow.dto.AccountStatsDtos.*;
import com.foodflow.entity.*;
import com.foodflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Uses updatedAt as a proxy for "when delivered", since our Order entity
// stamps updatedAt on every status change, and the final change is to DELIVERED.
@Service
@RequiredArgsConstructor
public class AccountStatsService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;

    public OwnerQuickStatsResponse getOwnerQuickStats(User owner) {
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(owner.getId());
        List<Long> restaurantIds = restaurants.stream().map(Restaurant::getId).collect(Collectors.toList());
        LocalDate today = LocalDate.now();

        List<Order> deliveredToday = restaurantIds.stream()
                .flatMap(id -> orderRepository.findByRestaurantIdAndStatus(id, OrderStatus.DELIVERED).stream())
                .filter(o -> o.getUpdatedAt() != null && o.getUpdatedAt().toLocalDate().equals(today))
                .collect(Collectors.toList());

        BigDecimal todayRevenue = deliveredToday.stream()
                .map(Order::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FoodItem topRated = restaurantIds.stream()
                .flatMap(id -> foodItemRepository.findByRestaurantId(id).stream())
                .filter(f -> f.getAverageRating() != null && f.getAverageRating() > 0)
                .max((a, b) -> Double.compare(a.getAverageRating(), b.getAverageRating()))
                .orElse(null);

        return new OwnerQuickStatsResponse(
                deliveredToday.size(),
                todayRevenue,
                topRated != null ? topRated.getName() : null,
                topRated != null ? topRated.getAverageRating() : null
        );
    }

    public AgentQuickStatsResponse getAgentQuickStats(User agent) {
        LocalDate today = LocalDate.now();
        List<Order> delivered = orderRepository.findByDeliveryAgentIdAndStatus(agent.getId(), OrderStatus.DELIVERED);

        List<Order> deliveredToday = delivered.stream()
                .filter(o -> o.getUpdatedAt() != null && o.getUpdatedAt().toLocalDate().equals(today))
                .collect(Collectors.toList());

        BigDecimal todayEarnings = deliveredToday.stream()
                .map(o -> o.getAgentEarning() != null ? o.getAgentEarning() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AgentQuickStatsResponse(deliveredToday.size(), todayEarnings, delivered.size());
    }
}