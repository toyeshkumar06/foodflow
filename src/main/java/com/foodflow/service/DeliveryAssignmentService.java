package com.foodflow.service;

import com.foodflow.entity.DeliveryAgentProfile;
import com.foodflow.entity.Order;
import com.foodflow.entity.OrderStatus;
import com.foodflow.entity.Restaurant;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.DeliveryAgentProfileRepository;
import com.foodflow.repository.OrderRepository;
import com.foodflow.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAssignmentService {

    private final DeliveryAgentProfileRepository deliveryAgentProfileRepository;
    private final OrderRepository orderRepository;

    public boolean assignNearestAgent(Order order) {
        Restaurant restaurant = order.getRestaurant();

        if (restaurant.getLatitude() == null || restaurant.getLongitude() == null) {
            throw ApiException.badRequest("Restaurant location is not set; cannot assign a delivery agent");
        }

        List<DeliveryAgentProfile> candidates = deliveryAgentProfileRepository.findByOnlineTrueAndBusyFalse()
                .stream()
                .filter(agent -> !order.getRejectedAgentIds().contains(agent.getUser().getId()))
                .filter(agent -> agent.getCurrentLatitude() != null && agent.getCurrentLongitude() != null)
                .toList();

        if (candidates.isEmpty()) {
            order.setDeliveryAgent(null);
            return false;
        }

        DeliveryAgentProfile nearest = candidates.stream()
                .min(Comparator.comparingDouble(agent -> GeoUtils.distanceKm(
                        restaurant.getLatitude(), restaurant.getLongitude(),
                        agent.getCurrentLatitude(), agent.getCurrentLongitude())))
                .orElseThrow();

        nearest.setBusy(true);
        deliveryAgentProfileRepository.save(nearest);

        order.setDeliveryAgent(nearest.getUser());
        order.setAgentConfirmed(false);
        return true;
    }

    // Called whenever an agent goes online — sweeps for any orders that were ready
    // for pickup but had nobody to assign to at the time, and tries again now.
    public void retryOrphanedAssignments() {
        List<Order> orphaned = orderRepository.findByStatusInAndDeliveryAgentIsNull(
                List.of(OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP));

        for (Order order : orphaned) {
            boolean assigned = assignNearestAgent(order);
            if (assigned) {
                orderRepository.save(order);
            }
        }
    }

    public void reassignAfterRejection(Order order, User rejectingAgent) {
        order.getRejectedAgentIds().add(rejectingAgent.getId());

        deliveryAgentProfileRepository.findByUserId(rejectingAgent.getId()).ifPresent(profile -> {
            profile.setBusy(false);
            deliveryAgentProfileRepository.save(profile);
        });

        order.setDeliveryAgent(null);
        assignNearestAgent(order);
    }
}