package com.foodflow.controller;

import com.foodflow.dto.AccountStatsDtos.AgentQuickStatsResponse;
import com.foodflow.dto.DeliveryDtos.AgentProfileResponse;
import com.foodflow.dto.DeliveryDtos.LocationRequest;
import com.foodflow.dto.OrderDtos.OrderResponse;
import com.foodflow.dto.OrderDtos.UpdateOrderStatusRequest;
import com.foodflow.entity.User;
import com.foodflow.service.AccountStatsService;
import com.foodflow.service.DeliveryAgentService;
import com.foodflow.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryAgentService deliveryAgentService;
    private final OrderService orderService;
    private final AccountStatsService accountStatsService;

    @GetMapping("/profile")
    public AgentProfileResponse getProfile(@AuthenticationPrincipal User agent) {
        return deliveryAgentService.getProfile(agent);
    }

    @GetMapping("/quick-stats")
    public AgentQuickStatsResponse getQuickStats(@AuthenticationPrincipal User agent) {
        return accountStatsService.getAgentQuickStats(agent);
    }

    @PostMapping("/go-online")
    public AgentProfileResponse goOnline(@Valid @RequestBody LocationRequest request,
                                          @AuthenticationPrincipal User agent) {
        return deliveryAgentService.goOnline(agent, request.getLatitude(), request.getLongitude());
    }

    @PostMapping("/go-offline")
    public void goOffline(@AuthenticationPrincipal User agent) {
        deliveryAgentService.goOffline(agent);
    }

    @PatchMapping("/location")
    public AgentProfileResponse updateLocation(@Valid @RequestBody LocationRequest request,
                                                @AuthenticationPrincipal User agent) {
        return deliveryAgentService.updateLocation(agent, request.getLatitude(), request.getLongitude());
    }

    @PostMapping("/orders/{orderId}/accept")
    public void acceptAssignment(@PathVariable Long orderId, @AuthenticationPrincipal User agent) {
        orderService.acceptAssignment(orderId, agent);
    }

    @PostMapping("/orders/{orderId}/reject")
    public void rejectAssignment(@PathVariable Long orderId, @AuthenticationPrincipal User agent) {
        orderService.rejectAssignment(orderId, agent);
    }

    @PatchMapping("/orders/{orderId}/status")
    public OrderResponse updateDeliveryStatus(@PathVariable Long orderId,
                                               @Valid @RequestBody UpdateOrderStatusRequest request,
                                               @AuthenticationPrincipal User agent) {
        return orderService.updateStatusByAgent(orderId, request.getStatus(), agent);
    }

    @GetMapping("/orders/current")
    public ResponseEntity<OrderResponse> getCurrentOrder(@AuthenticationPrincipal User agent) {
        OrderResponse response = orderService.getCurrentOrderForAgent(agent);
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/orders/history")
    public List<OrderResponse> getOrderHistory(@AuthenticationPrincipal User agent) {
        return orderService.getOrderHistoryForAgent(agent);
    }

    @GetMapping("/earnings")
    public BigDecimal getEarnings(@AuthenticationPrincipal User agent) {
        return deliveryAgentService.getEarnings(agent);
    }
}