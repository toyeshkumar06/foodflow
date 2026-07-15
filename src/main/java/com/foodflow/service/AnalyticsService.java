package com.foodflow.service;

import com.foodflow.dto.AnalyticsDtos.*;
import com.foodflow.entity.Order;
import com.foodflow.entity.OrderItem;
import com.foodflow.entity.OrderStatus;
import com.foodflow.entity.User;
import com.foodflow.repository.OrderItemRepository;
import com.foodflow.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Aggregation done in Java over the delivered-orders list rather than raw SQL —
// fine at MVP scale, and easy to explain in an interview: "would move this to a
// SQL GROUP BY / native query once order volume made in-memory aggregation too slow."
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantService restaurantService;

    public AdminOverviewResponse getAdminOverview() {
        List<Order> delivered = orderRepository.findByStatus(OrderStatus.DELIVERED);

        BigDecimal totalRevenue = delivered.stream()
                .map(Order::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String mostPopularRestaurant = delivered.stream()
                .collect(Collectors.groupingBy(o -> o.getRestaurant().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        String mostActiveCustomer = delivered.stream()
                .collect(Collectors.groupingBy(o -> o.getCustomer().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        String mostPopularFood = delivered.stream()
                .flatMap(o -> orderItemRepository.findByOrderId(o.getId()).stream())
                .collect(Collectors.groupingBy(OrderItem::getFoodNameSnapshot, Collectors.summingLong(OrderItem::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return new AdminOverviewResponse(totalRevenue, delivered.size(), mostPopularRestaurant, mostPopularFood, mostActiveCustomer);
    }

    public RestaurantAnalyticsResponse getRestaurantAnalytics(Long restaurantId, User owner) {
        restaurantService.getOwnedRestaurantOrThrow(restaurantId, owner);

        List<Order> delivered = orderRepository.findByRestaurantIdAndStatus(restaurantId, OrderStatus.DELIVERED);

        BigDecimal totalRevenue = delivered.stream()
                .map(Order::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TopItem> topItems = delivered.stream()
                .flatMap(o -> orderItemRepository.findByOrderId(o.getId()).stream())
                .collect(Collectors.groupingBy(OrderItem::getFoodNameSnapshot, Collectors.summingLong(OrderItem::getQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new TopItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<DailySales> last7Days = delivered.stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isAfter(sevenDaysAgo.minusDays(1)))
                .collect(Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate()))
                .entrySet().stream()
                .map(e -> new DailySales(
                        e.getKey(),
                        e.getValue().stream().map(Order::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
                        e.getValue().size()
                ))
                .sorted(Comparator.comparing(DailySales::getDate))
                .collect(Collectors.toList());

        return new RestaurantAnalyticsResponse(totalRevenue, delivered.size(), topItems, last7Days);
    }
}