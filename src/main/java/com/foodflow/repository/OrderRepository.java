package com.foodflow.repository;

import com.foodflow.entity.Order;
import com.foodflow.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByIdDesc(Long customerId);
    List<Order> findByRestaurantIdOrderByIdDesc(Long restaurantId);
    List<Order> findByDeliveryAgentIdAndStatusIn(Long deliveryAgentId, List<OrderStatus> statuses);
    List<Order> findByDeliveryAgentIdAndStatus(Long deliveryAgentId, OrderStatus status);
    long countByStatusIn(List<OrderStatus> statuses);
}