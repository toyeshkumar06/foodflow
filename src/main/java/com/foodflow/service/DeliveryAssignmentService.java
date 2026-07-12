package com.foodflow.service;

import com.foodflow.entity.DeliveryAgentProfile;
import com.foodflow.entity.Order;
import com.foodflow.entity.Restaurant;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.DeliveryAgentProfileRepository;
import com.foodflow.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAssignmentService {

    private final DeliveryAgentProfileRepository deliveryAgentProfileRepository;

    // The core algorithm: find online+free agents, exclude anyone who already rejected
    // this order, calculate distance to each, assign the nearest.
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
            return false; // no one available right now; can be retried later
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

    // Agent rejected: free them up, blacklist them for this order, try the next nearest.
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