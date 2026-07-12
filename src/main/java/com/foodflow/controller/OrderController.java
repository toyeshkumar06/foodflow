package com.foodflow.controller;

import com.foodflow.dto.OrderDtos.*;
import com.foodflow.entity.User;
import com.foodflow.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request,
                                     @AuthenticationPrincipal User customer) {
        return orderService.placeOrder(request, customer);
    }

    @PatchMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable Long orderId, @AuthenticationPrincipal User customer) {
        orderService.cancelOrder(orderId, customer);
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(@AuthenticationPrincipal User customer) {
        return orderService.getMyOrders(customer);
    }
}