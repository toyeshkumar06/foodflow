package com.foodflow.service;

import com.foodflow.dto.DeliveryDtos.AgentProfileResponse;
import com.foodflow.entity.DeliveryAgentProfile;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.DeliveryAgentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAgentService {

    private final DeliveryAgentProfileRepository profileRepository;
    private final DeliveryAssignmentService deliveryAssignmentService;

    public AgentProfileResponse goOnline(User agent, double latitude, double longitude) {
        DeliveryAgentProfile profile = getOrCreateProfile(agent);
        profile.setOnline(true);
        profile.setCurrentLatitude(latitude);
        profile.setCurrentLongitude(longitude);
        profileRepository.save(profile);

        deliveryAssignmentService.retryOrphanedAssignments();

        return toResponse(profile);
    }

    public void goOffline(User agent) {
        DeliveryAgentProfile profile = getOrCreateProfile(agent);
        if (profile.isBusy()) {
            throw ApiException.badRequest("Cannot go offline while on an active delivery");
        }
        profile.setOnline(false);
        profileRepository.save(profile);
    }

    public AgentProfileResponse updateLocation(User agent, double latitude, double longitude) {
        DeliveryAgentProfile profile = getOrCreateProfile(agent);
        profile.setCurrentLatitude(latitude);
        profile.setCurrentLongitude(longitude);
        profileRepository.save(profile);
        return toResponse(profile);
    }

    public BigDecimal getEarnings(User agent) {
        return getOrCreateProfile(agent).getTotalEarnings();
    }

    public AgentProfileResponse getProfile(User agent) {
        return toResponse(getOrCreateProfile(agent));
    }

    public DeliveryAgentProfile getOrCreateProfile(User agent) {
        return profileRepository.findByUserId(agent.getId())
                .orElseGet(() -> profileRepository.save(
                        DeliveryAgentProfile.builder().user(agent).online(false).busy(false)
                                .totalEarnings(BigDecimal.ZERO).build()));
    }

    private AgentProfileResponse toResponse(DeliveryAgentProfile p) {
        return new AgentProfileResponse(p.getId(), p.isOnline(), p.isBusy(),
                p.getCurrentLatitude(), p.getCurrentLongitude(), p.getTotalEarnings());
    }
}