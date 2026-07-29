package com.foodflow.service;

import com.foodflow.entity.*;
import com.foodflow.repository.DeliveryAgentProfileRepository;
import com.foodflow.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryAssignmentServiceTest {

    @Mock private DeliveryAgentProfileRepository deliveryAgentProfileRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private DeliveryAssignmentService deliveryAssignmentService;

    @Test
    void assignNearestAgent_picksClosestOfMultipleCandidates() {
        Restaurant restaurant = Restaurant.builder().id(1L).latitude(28.6139).longitude(77.2090).build();
        Order order = Order.builder().id(1L).restaurant(restaurant).build();

        User farUser = User.builder().id(10L).build();
        User nearUser = User.builder().id(11L).build();

        DeliveryAgentProfile farAgent = DeliveryAgentProfile.builder()
                .user(farUser).online(true).busy(false)
                .currentLatitude(29.0).currentLongitude(78.0)
                .build();
        DeliveryAgentProfile nearAgent = DeliveryAgentProfile.builder()
                .user(nearUser).online(true).busy(false)
                .currentLatitude(28.62).currentLongitude(77.21)
                .build();

        when(deliveryAgentProfileRepository.findByOnlineTrueAndBusyFalse())
                .thenReturn(List.of(farAgent, nearAgent));

        boolean assigned = deliveryAssignmentService.assignNearestAgent(order);

        assertTrue(assigned);
        assertEquals(nearUser.getId(), order.getDeliveryAgent().getId());
    }

    @Test
    void assignNearestAgent_returnsFalseWhenNoAgentsOnline() {
        Restaurant restaurant = Restaurant.builder().id(1L).latitude(28.6139).longitude(77.2090).build();
        Order order = Order.builder().id(1L).restaurant(restaurant).build();

        when(deliveryAgentProfileRepository.findByOnlineTrueAndBusyFalse()).thenReturn(List.of());

        boolean assigned = deliveryAssignmentService.assignNearestAgent(order);

        assertFalse(assigned);
        assertNull(order.getDeliveryAgent());
    }

    @Test
    void assignNearestAgent_excludesAgentsWhoAlreadyRejectedThisOrder() {
        Restaurant restaurant = Restaurant.builder().id(1L).latitude(28.6139).longitude(77.2090).build();
        User rejectingUser = User.builder().id(10L).build();

        Order order = Order.builder().id(1L).restaurant(restaurant).build();
        order.getRejectedAgentIds().add(rejectingUser.getId());

        DeliveryAgentProfile rejectingAgent = DeliveryAgentProfile.builder()
                .user(rejectingUser).online(true).busy(false)
                .currentLatitude(28.62).currentLongitude(77.21)
                .build();

        when(deliveryAgentProfileRepository.findByOnlineTrueAndBusyFalse())
                .thenReturn(List.of(rejectingAgent));

        boolean assigned = deliveryAssignmentService.assignNearestAgent(order);

        assertFalse(assigned);
    }
}