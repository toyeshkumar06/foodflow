package com.foodflow.controller;

import com.foodflow.dto.OrderDtos.OrderResponse;
import com.foodflow.dto.OrderDtos.UpdateOrderStatusRequest;
import com.foodflow.entity.User;
import com.foodflow.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant-owner")
@RequiredArgsConstructor
public class RestaurantOrderController {

    private final OrderService orderService;

    @GetMapping("/restaurants/{restaurantId}/orders")
    public List<OrderResponse> getIncomingOrders(@PathVariable Long restaurantId,
                                                  @AuthenticationPrincipal User owner) {
        return orderService.getRestaurantOrders(restaurantId, owner);
    }

    @PatchMapping("/orders/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable Long orderId,
                                       @Valid @RequestBody UpdateOrderStatusRequest request,
                                       @AuthenticationPrincipal User owner) {
        return orderService.updateStatusByOwner(orderId, request.getStatus(), owner);
    }
}